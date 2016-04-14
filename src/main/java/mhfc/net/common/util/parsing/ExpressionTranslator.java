package mhfc.net.common.util.parsing;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import mhfc.net.common.util.CompositeException;
import mhfc.net.common.util.parsing.syntax.IBasicSequence;
import mhfc.net.common.util.parsing.syntax.literals.ConstantLiteral;
import mhfc.net.common.util.parsing.syntax.literals.FunctionCallLiteral;
import mhfc.net.common.util.parsing.syntax.literals.IExpression;
import mhfc.net.common.util.parsing.syntax.literals.IdentifierLiteral;
import mhfc.net.common.util.parsing.syntax.operators.ArgumentContinuationOperator;
import mhfc.net.common.util.parsing.syntax.operators.FunctionOperator;
import mhfc.net.common.util.parsing.syntax.operators.IBinaryOperator;
import mhfc.net.common.util.parsing.syntax.operators.MemberOperator;
import mhfc.net.common.util.parsing.syntax.tree.AST;
import mhfc.net.common.util.parsing.syntax.tree.SyntaxBuilder;
import net.minecraft.command.SyntaxErrorException;

public class ExpressionTranslator {
	private static final Pattern commentPattern = Pattern.compile("/\\*.*?\\*/");

	private static final SyntaxBuilder TREE_BUILDER = new SyntaxBuilder();
	private static final int VAL_EXPRESSION_ID;
	private static final int VAL_IDENTIFIER_ID;
	private static final int VAL_FUNCTIONCALL_ID;

	private static final int OP_MEMBERACCESS_ID;
	private static final int OP_FUNCTIONCALL_ID;
	private static final int OP_ARGUMENTCONTINUE_ID;

	static {
		VAL_EXPRESSION_ID = TREE_BUILDER.registerTerminal(IExpression.class);
		VAL_IDENTIFIER_ID = TREE_BUILDER.registerTerminal(IdentifierLiteral.class, VAL_EXPRESSION_ID);
		VAL_FUNCTIONCALL_ID = TREE_BUILDER.registerTerminal(FunctionCallLiteral.class, VAL_EXPRESSION_ID);

		OP_MEMBERACCESS_ID = TREE_BUILDER.registerBinaryOperator(
				MemberOperator.class,
				VAL_EXPRESSION_ID,
				VAL_IDENTIFIER_ID,
				VAL_EXPRESSION_ID,
				true);
		OP_FUNCTIONCALL_ID = TREE_BUILDER.registerBinaryOperator(
				FunctionOperator.class,
				VAL_EXPRESSION_ID,
				VAL_EXPRESSION_ID,
				VAL_FUNCTIONCALL_ID,
				true);
		OP_ARGUMENTCONTINUE_ID = TREE_BUILDER.registerBinaryOperator(
				ArgumentContinuationOperator.class,
				VAL_FUNCTIONCALL_ID,
				VAL_EXPRESSION_ID,
				VAL_FUNCTIONCALL_ID,
				true);
		TREE_BUILDER.declarePrecedence(OP_MEMBERACCESS_ID, OP_FUNCTIONCALL_ID);
		TREE_BUILDER.declarePrecedence(OP_ARGUMENTCONTINUE_ID, OP_FUNCTIONCALL_ID);
		TREE_BUILDER.declarePrecedence(OP_MEMBERACCESS_ID, OP_ARGUMENTCONTINUE_ID);

		TREE_BUILDER.validate();
	}

	private class Identifier implements IBasicSequence {
		private StringBuilder sb = new StringBuilder();

		@Override
		public SiftResult accepting(int cp) {
			if (sb.length() == 0) {
				if (Character.isJavaIdentifierStart(cp)) {
					sb.appendCodePoint(cp);
					return SiftResult.ACCEPTED;
				}
				return SiftResult.REJCECTED;
			}
			if (Character.isJavaIdentifierPart(cp)) {
				sb.appendCodePoint(cp);
				return SiftResult.ACCEPTED;
			}
			return SiftResult.FINISHED;
		}

		@Override
		public void reset() {
			sb.setLength(0);
		}

		@Override
		public SiftResult endOfStream() {
			return this.sb.length() == 0 ? SiftResult.REJCECTED : SiftResult.FINISHED;
		}

		@Override
		public void pushOnto(AST ast) {
			ast.pushValue(VAL_IDENTIFIER_ID, new IdentifierLiteral(context, sb.toString()));
		}
	}

	private class Whitespace implements IBasicSequence {
		private int length = 0;

		@Override
		public SiftResult accepting(int cp) {
			length++;
			if (Character.isWhitespace(cp)) {
				return SiftResult.ACCEPTED;
			}
			if (length > 1) {
				return SiftResult.FINISHED;
			}
			return SiftResult.REJCECTED;
		}

		@Override
		public void reset() {
			length = 0;
		}

		@Override
		public SiftResult endOfStream() {
			return SiftResult.FINISHED;
		}

		@Override
		public void pushOnto(AST ast) { /* ignore */ }
	}

	private static class StringConstant implements IBasicSequence {
		private static enum State {
			BEGIN,
			ACTIVE,
			ENDED;
		}

		private StringBuilder string = new StringBuilder();
		private State state = State.BEGIN;
		private boolean escaped = false;
		private List<Throwable> errors = new ArrayList<>();
		private int encountered = 0;

		@Override
		public SiftResult accepting(int cp) {
			if (state == State.ENDED) {
				return SiftResult.FINISHED;
			}
			if (state == State.BEGIN) {
				if (cp == '\"') {
					this.state = State.ACTIVE;
					this.encountered++;
					return SiftResult.ACCEPTED;
				}
				return SiftResult.REJCECTED;
			}
			if (!escaped) {
				if (cp == '\\') {
					escaped = true;
					return SiftResult.ACCEPTED;
				}
				if (cp == '\"') {
					this.state = State.ENDED;
					return SiftResult.ACCEPTED;
				}
				string.appendCodePoint(cp);
				this.encountered++;
				return SiftResult.ACCEPTED;
			}
			switch (cp) {
			// \b \t \n \f \r \" \' \\
			case 'b':
				string.appendCodePoint('\b');
				break;
			case 't':
				string.appendCodePoint('\t');
				break;
			case 'n':
				string.appendCodePoint('\n');
				break;
			case 'f':
				string.appendCodePoint('\f');
				break;
			case 'r':
				string.appendCodePoint('\r');
				break;
			case '"':
				string.appendCodePoint('\"');
				break;
			case '\'':
				string.appendCodePoint('\'');
				break;
			case '\\':
				string.appendCodePoint('\\');
				break;
			default: {
				// Illegal escape sequence
				String error = "Illegal escape sequence \\" + String.valueOf(Character.toChars(cp)) + " at index "
						+ this.encountered;
				this.encountered++;
				this.errors.add(new IllegalArgumentException(error));
			}
			}
			this.escaped = false;
			return SiftResult.ACCEPTED;
		}

		@Override
		public void reset() {
			state = State.BEGIN;
			escaped = false;
			string.setLength(0);
			errors.clear();
			encountered = 0;
		}

		@Override
		public SiftResult endOfStream() {
			return state == State.ENDED ? SiftResult.REJCECTED : SiftResult.FINISHED;
		}

		@Override
		public void pushOnto(AST ast) {
			Holder value;
			if (this.errors.size() == 0) {
				value = Holder.valueOfIfPresent(this.string.toString());
			} else {
				value = Holder.failedComputation(new CompositeException(this.errors));
			}
			ast.pushValue(VAL_EXPRESSION_ID, new ConstantLiteral(value));
		}
	}

	private static class IntegerConstant implements IBasicSequence {
		private static enum State {
			START,
			POSTSIGN,
			NUMBER;
		}

		private int base;
		private int length;
		private State state;
		private StringBuilder string = new StringBuilder();

		public IntegerConstant() {
			reset();
		}

		@Override
		public SiftResult accepting(int cp) {
			if (state == State.START) {
				if (cp == '+' || cp == '-') {
					string.appendCodePoint(cp);
					state = State.POSTSIGN;
					return SiftResult.ACCEPTED;
				}
			}
			if (state == State.START || state == State.POSTSIGN) {
				if (cp == '0') {
					base = 0;
					return SiftResult.ACCEPTED;
				}
				if (cp == 'o' && base == 0) {
					base = 8;
					state = State.NUMBER;
					return SiftResult.ACCEPTED;
				}
				if (cp == 'x' && base == 0) {
					base = 16;
					state = State.NUMBER;
					return SiftResult.ACCEPTED;
				}
				if (cp == 'b' && base == 0) {
					base = 2;
					state = State.NUMBER;
					return SiftResult.ACCEPTED;
				}
				state = State.NUMBER;
				if (base == 0) {
					// Literal 0, followed...
					string.appendCodePoint('0');
					base = 10;
					return SiftResult.FINISHED;
				}
			}
			if (state != State.NUMBER) {
				return SiftResult.REJCECTED;
			}
			if (Character.digit(cp, base) != -1) {
				length++;
				string.appendCodePoint(cp);
				return SiftResult.ACCEPTED;
			}
			if (length > 0) {
				return SiftResult.FINISHED;
			}
			return SiftResult.REJCECTED;
		}

		@Override
		public SiftResult endOfStream() {
			return length > 0 ? SiftResult.FINISHED : SiftResult.REJCECTED;
		}

		@Override
		public void reset() {
			base = 10;
			length = 0;
			state = State.START;
			string.setLength(0);
		}

		@Override
		public void pushOnto(AST ast) {
			Holder value;
			try {
				int val = Integer.parseInt(string.toString(), base);
				value = Holder.valueOf(val);
			} catch (NumberFormatException nfe) {
				value = Holder.failedComputation(nfe);
			}
			ast.pushValue(VAL_EXPRESSION_ID, new ConstantLiteral(value));
		}

	}

	private IBasicSequence makeBinaryOperator(int matchingChar, int ID, Supplier<IBinaryOperator<?, ?, ?>> opSupplier) {
		return new IBasicSequence() {
			private boolean matched;

			@Override
			public void reset() {
				matched = false;
			}

			@Override
			public void pushOnto(AST ast) throws SyntaxErrorException {
				ast.pushBinaryOperator(ID, opSupplier.get());
			}

			@Override
			public SiftResult endOfStream() {
				if (matched) {
					return SiftResult.FINISHED;
				}
				return SiftResult.REJCECTED;
			}

			@Override
			public SiftResult accepting(int cp) {
				if (matched) {
					return SiftResult.FINISHED;
				}
				if (matchingChar == cp) {
					matched = true;
					return SiftResult.ACCEPTED;
				}
				return SiftResult.REJCECTED;
			}
		};
	}

	private final List<IBasicSequence> sequences = new ArrayList<>();
	private final Context context;

	public ExpressionTranslator(Context context) {
		this.context = context;
		sequences.add(new Whitespace());

		sequences.add(makeBinaryOperator('.', OP_MEMBERACCESS_ID, MemberOperator::new));
		sequences.add(makeBinaryOperator('|', OP_FUNCTIONCALL_ID, FunctionOperator::new));
		sequences.add(makeBinaryOperator(':', OP_ARGUMENTCONTINUE_ID, ArgumentContinuationOperator::new));

		sequences.add(this.new Identifier());
		sequences.add(new StringConstant());
		sequences.add(new IntegerConstant());
		sequences.add(new IntegerConstant());

	}

	public IValueHolder parse(String expression) {
		// Replace all comments
		String noComment = commentPattern.matcher(expression).replaceAll(" ");
		try {
			return parseCleaned(noComment);
		} catch (SyntaxErrorException see) {
			return Holder.failedComputation(see);
		}
	}

	private IValueHolder parseCleaned(String cleanExpression) {
		// Cleaned as in: does only contain spaces as whitespace, doesn't
		// contain any comments and only one sequential whitespace.
		// So we can reset
		AST parseTree = TREE_BUILDER.newParseTree();

		IntBuffer expressionBuf = IntBuffer.wrap(cleanExpression.codePoints().toArray());
		expressionBuf.mark();

		Iterator<IBasicSequence> basicSequences = sequences.iterator();
		IBasicSequence currentSequence = nextSequenceOrSyntaxError(basicSequences, expressionBuf);

		parse_loop: while (true) {
			if (!expressionBuf.hasRemaining()) {
				switch (currentSequence.endOfStream()) {
				case ACCEPTED:
					throw new SyntaxErrorException("Can't accept end-of-stream, only FINISHED or REJECTED");
				case FINISHED:
					currentSequence.pushOnto(parseTree);
					break parse_loop;
				case REJCECTED:
				default:
					currentSequence.reset();
					expressionBuf.reset();
					currentSequence = nextSequenceOrSyntaxError(basicSequences, expressionBuf);
					break;
				}
				continue;
			}
			int cp = expressionBuf.get();
			switch (currentSequence.accepting(cp)) {
			case ACCEPTED:
				break;
			case FINISHED:
				// Rewind by 1, and push the syntax stack
				expressionBuf.position(expressionBuf.position() - 1);
				expressionBuf.mark();
				currentSequence.pushOnto(parseTree);
				basicSequences = sequences.iterator();
				currentSequence = nextSequenceOrSyntaxError(basicSequences, expressionBuf);
				break;
			case REJCECTED:
			default:
				// Reset to the mark, try next sequence
				expressionBuf.reset();
				currentSequence = nextSequenceOrSyntaxError(basicSequences, expressionBuf);
				break;
			}
		}
		return ((IExpression) parseTree.getOverallValue()).asValue();
	}

	private IBasicSequence nextSequenceOrSyntaxError(Iterator<IBasicSequence> sequences, IntBuffer stream) {
		if (sequences.hasNext()) {
			IBasicSequence next = sequences.next();
			next.reset();
			return next;
		}
		// Collect the next few integers
		int collected = Math.min(stream.remaining(), 20);
		int position = stream.position();
		int[] out = new int[collected];
		stream.get(out);
		String asString = new String(out, 0, collected);
		String message = "Encountered unexpected sequence: (first 20) \"" + asString + "\" at " + position;
		throw new SyntaxErrorException(message);
	}
}

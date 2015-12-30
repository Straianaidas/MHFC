package mhfc.net.common.util.parsing.expressions;

import static mhfc.net.common.util.Utilities.mapAll;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

import mhfc.net.common.util.parsing.Context;
import mhfc.net.common.util.parsing.Holder;
import mhfc.net.common.util.parsing.IExpression;
import mhfc.net.common.util.parsing.IValueHolder;
import mhfc.net.common.util.parsing.expressions.UnresolvedContextVariableException.UnresolvedFunctionException;
import mhfc.net.common.util.parsing.valueholders.FreeFunctionInvocation;

public class FreeFunctionCall implements IExpression {
	private final IExpression[] args;
	private final String name;

	public FreeFunctionCall(String name, IExpression... expressions) {
		this.name = Objects.requireNonNull(name);
		this.args = mhfc.net.common.util.Objects.requireNonNullDeep(Arrays.copyOf(expressions, expressions.length));
	}

	@Override
	public IValueHolder resolveAgainst(Context context) {
		return new IValueHolder() {

			private IValueHolder resolveNow() {
				Function<Arguments, IValueHolder> func = context.getFilter(name);
				if (func == null) {
					return Holder.failedComputation(new UnresolvedFunctionException(name, context));
				}
				return new FreeFunctionInvocation(
						func,
						mapAll(e -> e.resolveAgainst(context), FreeFunctionCall.this.args, IValueHolder[]::new));
			}

			@Override
			public Holder snapshot() {
				return resolveNow().snapshot();
			}

			@Override
			public IValueHolder snapshotClass() {
				return resolveNow().snapshotClass();
			}

			@Override
			public Class<?> getType() {
				return resolveNow().getType();
			}

		};
	}

}
package mhfc.net.common.util.parsing;

import java.lang.invoke.MethodHandle;
import java.util.Optional;

import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;

import mhfc.net.common.util.reflection.FieldHelper;
import mhfc.net.common.util.reflection.MethodHelper;
import mhfc.net.common.util.reflection.OverloadedMethod;

public class ReflectionTest {
	public class TestClass {
		public String member = "Found";

		public String method(String argument) {
			return argument;
		}

		public int method(float argument) {
			return (int) argument;
		}
	}

	@Test
	public void findMember() {
		Optional<MethodHandle> f = FieldHelper.find(TestClass.class, "member");
		Assert.assertTrue(f.toString(), f.isPresent());
		Assert.assertThat(f.get().type().returnType(), IsEqual.equalTo(String.class));
	}

	@Test
	public void findMethod() {
		Optional<OverloadedMethod> methods = MethodHelper.find(TestClass.class, "method");
		Assert.assertTrue(methods.toString(), methods.isPresent());
	}

	@Test
	public void disambiguate() throws Throwable {
		TestClass instance = new TestClass();
		Optional<OverloadedMethod> methods = MethodHelper.find(TestClass.class, "method");
		OverloadedMethod ms = methods.get();

		Optional<MethodHandle> method = ms.disambiguate(String.class);
		Assert.assertTrue(methods.toString(), method.isPresent());
		Assert.assertThat(method.get().invokeWithArguments(instance, "test"), IsEqual.equalTo("test"));

		method = ms.disambiguate(float.class);
		Assert.assertTrue(methods.toString(), method.isPresent());
		Assert.assertThat(method.get().invokeWithArguments(instance, 1.1f), IsEqual.equalTo(1));
	}
}

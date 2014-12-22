package net.bytebuddy.instrumentation.method;

import net.bytebuddy.instrumentation.attribute.annotation.AnnotationList;
import net.bytebuddy.instrumentation.type.TypeDescription;
import net.bytebuddy.instrumentation.type.TypeList;
import net.bytebuddy.test.packaging.VisibilityMethodTestHelper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.asm.Type;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class AbstractMethodDescriptionTest {

    private Method firstMethod, secondMethod, thirdMethod;
    private Constructor<?> firstConstructor, secondConstructor;

    private static int hashCode(Method method) {
        int hashCode = new TypeDescription.ForLoadedType(method.getDeclaringClass()).hashCode();
        hashCode = 31 * hashCode + method.getName().hashCode();
        hashCode = 31 * hashCode + new TypeDescription.ForLoadedType(method.getReturnType()).hashCode();
        return 31 * hashCode + new TypeList.ForLoadedType(method.getParameterTypes()).hashCode();
    }

    private static int hashCode(Constructor<?> constructor) {
        int hashCode = new TypeDescription.ForLoadedType(constructor.getDeclaringClass()).hashCode();
        hashCode = 31 * hashCode + MethodDescription.CONSTRUCTOR_INTERNAL_NAME.hashCode();
        hashCode = 31 * hashCode + new TypeDescription.ForLoadedType(void.class).hashCode();
        return 31 * hashCode + new TypeList.ForLoadedType(constructor.getParameterTypes()).hashCode();
    }

    protected abstract MethodDescription describe(Method method);

    protected abstract MethodDescription describe(Constructor<?> constructor);

    @Before
    public void setUp() throws Exception {
        firstMethod = Sample.class.getDeclaredMethod("first");
        secondMethod = Sample.class.getDeclaredMethod("second", String.class, long.class);
        thirdMethod = Sample.class.getDeclaredMethod("third", Object[].class, int[].class);
        firstConstructor = Sample.class.getDeclaredConstructor(Void.class);
        secondConstructor = Sample.class.getDeclaredConstructor(int[].class, long.class);
    }

    @Test
    public void testPrecondition() throws Exception {
        assertThat(describe(firstMethod), not(equalTo(describe(secondMethod))));
        assertThat(describe(firstMethod), not(equalTo(describe(thirdMethod))));
        assertThat(describe(firstMethod), equalTo(describe(firstMethod)));
        assertThat(describe(secondMethod), equalTo(describe(secondMethod)));
        assertThat(describe(thirdMethod), equalTo(describe(thirdMethod)));
        assertThat(describe(firstMethod), is((MethodDescription) new MethodDescription.ForLoadedMethod(firstMethod)));
        assertThat(describe(secondMethod), is((MethodDescription) new MethodDescription.ForLoadedMethod(secondMethod)));
        assertThat(describe(thirdMethod), is((MethodDescription) new MethodDescription.ForLoadedMethod(thirdMethod)));
        assertThat(describe(firstConstructor), not(equalTo(describe(secondConstructor))));
        assertThat(describe(firstConstructor), equalTo(describe(firstConstructor)));
        assertThat(describe(secondConstructor), equalTo(describe(secondConstructor)));
        assertThat(describe(firstConstructor), is((MethodDescription) new MethodDescription.ForLoadedConstructor(firstConstructor)));
        assertThat(describe(secondConstructor), is((MethodDescription) new MethodDescription.ForLoadedConstructor(secondConstructor)));
    }

    @Test
    public void testReturnType() throws Exception {
        assertThat(describe(firstMethod).getReturnType(),
                is((TypeDescription) new TypeDescription.ForLoadedType(firstMethod.getReturnType())));
        assertThat(describe(secondMethod).getReturnType(),
                is((TypeDescription) new TypeDescription.ForLoadedType(secondMethod.getReturnType())));
        assertThat(describe(thirdMethod).getReturnType(),
                is((TypeDescription) new TypeDescription.ForLoadedType(thirdMethod.getReturnType())));
        assertThat(describe(firstConstructor).getReturnType(), is((TypeDescription) new TypeDescription.ForLoadedType(void.class)));
        assertThat(describe(secondConstructor).getReturnType(), is((TypeDescription) new TypeDescription.ForLoadedType(void.class)));
    }

    @Test
    public void testParameterTypes() throws Exception {
        assertThat(describe(firstMethod).getParameterTypes(),
                is((TypeList) new TypeList.ForLoadedType(firstMethod.getParameterTypes())));
        assertThat(describe(secondMethod).getParameterTypes(),
                is((TypeList) new TypeList.ForLoadedType(secondMethod.getParameterTypes())));
        assertThat(describe(thirdMethod).getParameterTypes(),
                is((TypeList) new TypeList.ForLoadedType(thirdMethod.getParameterTypes())));
        assertThat(describe(firstConstructor).getParameterTypes(),
                is((TypeList) new TypeList.ForLoadedType(firstConstructor.getParameterTypes())));
        assertThat(describe(secondConstructor).getParameterTypes(),
                is((TypeList) new TypeList.ForLoadedType(secondConstructor.getParameterTypes())));
    }

    @Test
    public void testMethodName() throws Exception {
        assertThat(describe(firstMethod).getName(), is(firstMethod.getName()));
        assertThat(describe(secondMethod).getName(), is(secondMethod.getName()));
        assertThat(describe(thirdMethod).getName(), is(thirdMethod.getName()));
        assertThat(describe(firstConstructor).getName(), is(firstConstructor.getDeclaringClass().getName()));
        assertThat(describe(secondConstructor).getName(), is(secondConstructor.getDeclaringClass().getName()));
        assertThat(describe(firstMethod).getInternalName(), is(firstMethod.getName()));
        assertThat(describe(secondMethod).getInternalName(), is(secondMethod.getName()));
        assertThat(describe(thirdMethod).getInternalName(), is(thirdMethod.getName()));
        assertThat(describe(firstConstructor).getInternalName(), is(MethodDescription.CONSTRUCTOR_INTERNAL_NAME));
        assertThat(describe(secondConstructor).getInternalName(), is(MethodDescription.CONSTRUCTOR_INTERNAL_NAME));
    }

    @Test
    public void testDescriptor() throws Exception {
        assertThat(describe(firstMethod).getDescriptor(), is(Type.getMethodDescriptor(firstMethod)));
        assertThat(describe(secondMethod).getDescriptor(), is(Type.getMethodDescriptor(secondMethod)));
        assertThat(describe(thirdMethod).getDescriptor(), is(Type.getMethodDescriptor(thirdMethod)));
        assertThat(describe(firstConstructor).getDescriptor(), is(Type.getConstructorDescriptor(firstConstructor)));
        assertThat(describe(secondConstructor).getDescriptor(), is(Type.getConstructorDescriptor(secondConstructor)));
    }

    @Test
    public void testMethodModifiers() throws Exception {
        assertThat(describe(firstMethod).getModifiers(), is(firstMethod.getModifiers()));
        assertThat(describe(secondMethod).getModifiers(), is(secondMethod.getModifiers()));
        assertThat(describe(thirdMethod).getModifiers(), is(thirdMethod.getModifiers()));
        assertThat(describe(firstConstructor).getModifiers(), is(firstConstructor.getModifiers()));
        assertThat(describe(secondConstructor).getModifiers(), is(secondConstructor.getModifiers()));
    }

    @Test
    public void testMethodDeclaringType() throws Exception {
        assertThat(describe(firstMethod).getDeclaringElement(), is((TypeDescription) new TypeDescription.ForLoadedType(firstMethod.getDeclaringClass())));
        assertThat(describe(secondMethod).getDeclaringElement(), is((TypeDescription) new TypeDescription.ForLoadedType(secondMethod.getDeclaringClass())));
        assertThat(describe(thirdMethod).getDeclaringElement(), is((TypeDescription) new TypeDescription.ForLoadedType(thirdMethod.getDeclaringClass())));
        assertThat(describe(firstConstructor).getDeclaringElement(), is((TypeDescription) new TypeDescription.ForLoadedType(firstConstructor.getDeclaringClass())));
        assertThat(describe(secondConstructor).getDeclaringElement(), is((TypeDescription) new TypeDescription.ForLoadedType(secondConstructor.getDeclaringClass())));
    }

    @Test
    public void testHashCode() throws Exception {
        assertThat(describe(firstMethod).hashCode(), is(hashCode(firstMethod)));
        assertThat(describe(secondMethod).hashCode(), is(hashCode(secondMethod)));
        assertThat(describe(thirdMethod).hashCode(), is(hashCode(thirdMethod)));
        assertThat(describe(firstMethod).hashCode(), not(is(hashCode(secondMethod))));
        assertThat(describe(firstMethod).hashCode(), not(is(hashCode(thirdMethod))));
        assertThat(describe(firstMethod).hashCode(), not(is(hashCode(firstConstructor))));
        assertThat(describe(firstMethod).hashCode(), not(is(hashCode(secondConstructor))));
        assertThat(describe(firstConstructor).hashCode(), is(hashCode(firstConstructor)));
        assertThat(describe(secondConstructor).hashCode(), is(hashCode(secondConstructor)));
        assertThat(describe(firstConstructor).hashCode(), not(is(hashCode(firstMethod))));
        assertThat(describe(firstConstructor).hashCode(), not(is(hashCode(secondMethod))));
        assertThat(describe(firstConstructor).hashCode(), not(is(hashCode(thirdMethod))));
        assertThat(describe(firstConstructor).hashCode(), not(is(hashCode(secondConstructor))));
    }

    @Test
    public void testEqualsMethod() throws Exception {
        MethodDescription identical = describe(firstMethod);
        assertThat(identical, equalTo(identical));
        assertThat(describe(firstMethod), equalTo(describe(firstMethod)));
        assertThat(describe(firstMethod), not(equalTo(describe(secondMethod))));
        assertThat(describe(firstMethod), not(equalTo(describe(thirdMethod))));
        assertThat(describe(firstMethod), not(equalTo(describe(firstConstructor))));
        assertThat(describe(firstMethod), not(equalTo(describe(secondConstructor))));
        assertThat(describe(firstMethod), equalTo((MethodDescription) new MethodDescription.ForLoadedMethod(firstMethod)));
        assertThat(describe(firstMethod), not(equalTo((MethodDescription) new MethodDescription.ForLoadedMethod(secondMethod))));
        assertThat(describe(firstMethod), not(equalTo((MethodDescription) new MethodDescription.ForLoadedMethod(thirdMethod))));
        assertThat(describe(firstMethod), not(equalTo((MethodDescription) new MethodDescription.ForLoadedConstructor(firstConstructor))));
        assertThat(describe(firstMethod), not(equalTo((MethodDescription) new MethodDescription.ForLoadedConstructor(secondConstructor))));
        MethodDescription equalMethod = mock(MethodDescription.class);
        when(equalMethod.getInternalName()).thenReturn(firstMethod.getName());
        when(equalMethod.getDeclaringElement()).thenReturn(new TypeDescription.ForLoadedType(firstMethod.getDeclaringClass()));
        when(equalMethod.getReturnType()).thenReturn(new TypeDescription.ForLoadedType(firstMethod.getReturnType()));
        when(equalMethod.getParameterTypes()).thenReturn(new TypeList.ForLoadedType(firstMethod.getParameterTypes()));
        assertThat(describe(firstMethod), equalTo(equalMethod));
        MethodDescription equalMethodButName = mock(MethodDescription.class);
        when(equalMethodButName.getInternalName()).thenReturn(secondMethod.getName());
        when(equalMethodButName.getDeclaringElement()).thenReturn(new TypeDescription.ForLoadedType(firstMethod.getDeclaringClass()));
        when(equalMethodButName.getReturnType()).thenReturn(new TypeDescription.ForLoadedType(firstMethod.getReturnType()));
        when(equalMethodButName.getParameterTypes()).thenReturn(new TypeList.ForLoadedType(firstMethod.getParameterTypes()));
        assertThat(describe(firstMethod), not(equalTo(equalMethodButName)));
        MethodDescription equalMethodButReturnType = mock(MethodDescription.class);
        when(equalMethodButReturnType.getInternalName()).thenReturn(firstMethod.getName());
        when(equalMethodButReturnType.getDeclaringElement()).thenReturn(new TypeDescription.ForLoadedType(Object.class));
        when(equalMethodButReturnType.getReturnType()).thenReturn(new TypeDescription.ForLoadedType(firstMethod.getReturnType()));
        when(equalMethodButReturnType.getParameterTypes()).thenReturn(new TypeList.ForLoadedType(firstMethod.getParameterTypes()));
        assertThat(describe(firstMethod), not(equalTo(equalMethodButReturnType)));
        MethodDescription equalMethodButDeclaringType = mock(MethodDescription.class);
        when(equalMethodButDeclaringType.getInternalName()).thenReturn(firstMethod.getName());
        when(equalMethodButDeclaringType.getDeclaringElement()).thenReturn(new TypeDescription.ForLoadedType(firstMethod.getDeclaringClass()));
        when(equalMethodButDeclaringType.getReturnType()).thenReturn(new TypeDescription.ForLoadedType(secondMethod.getReturnType()));
        when(equalMethodButDeclaringType.getParameterTypes()).thenReturn(new TypeList.ForLoadedType(firstMethod.getParameterTypes()));
        assertThat(describe(firstMethod), not(equalTo(equalMethodButDeclaringType)));
        MethodDescription equalMethodButParameterTypes = mock(MethodDescription.class);
        when(equalMethodButParameterTypes.getInternalName()).thenReturn(firstMethod.getName());
        when(equalMethodButParameterTypes.getDeclaringElement()).thenReturn(new TypeDescription.ForLoadedType(firstMethod.getDeclaringClass()));
        when(equalMethodButParameterTypes.getReturnType()).thenReturn(new TypeDescription.ForLoadedType(firstMethod.getReturnType()));
        when(equalMethodButParameterTypes.getParameterTypes()).thenReturn(new TypeList.ForLoadedType(secondMethod.getParameterTypes()));
        assertThat(describe(firstMethod), not(equalTo(equalMethodButParameterTypes)));
        assertThat(describe(firstMethod), not(equalTo(new Object())));
        assertThat(describe(firstMethod), not(equalTo(null)));
    }

    @Test
    public void testEqualsConstructor() throws Exception {
        MethodDescription identical = describe(firstConstructor);
        assertThat(identical, equalTo(identical));
        assertThat(describe(firstConstructor), equalTo(describe(firstConstructor)));
        assertThat(describe(firstConstructor), not(equalTo(describe(secondConstructor))));
        assertThat(describe(firstConstructor), not(equalTo(describe(firstMethod))));
        assertThat(describe(firstConstructor), not(equalTo(describe(secondMethod))));
        assertThat(describe(firstConstructor), not(equalTo(describe(thirdMethod))));
        assertThat(describe(firstConstructor), equalTo((MethodDescription) new MethodDescription.ForLoadedConstructor(firstConstructor)));
        assertThat(describe(firstConstructor), not(equalTo((MethodDescription) new MethodDescription.ForLoadedConstructor(secondConstructor))));
        assertThat(describe(firstConstructor), not(equalTo((MethodDescription) new MethodDescription.ForLoadedMethod(firstMethod))));
        assertThat(describe(firstConstructor), not(equalTo((MethodDescription) new MethodDescription.ForLoadedMethod(secondMethod))));
        assertThat(describe(firstConstructor), not(equalTo((MethodDescription) new MethodDescription.ForLoadedMethod(thirdMethod))));
        MethodDescription equalMethod = mock(MethodDescription.class);
        when(equalMethod.getInternalName()).thenReturn(MethodDescription.CONSTRUCTOR_INTERNAL_NAME);
        when(equalMethod.getDeclaringElement()).thenReturn(new TypeDescription.ForLoadedType(firstConstructor.getDeclaringClass()));
        when(equalMethod.getReturnType()).thenReturn(new TypeDescription.ForLoadedType(void.class));
        when(equalMethod.getParameterTypes()).thenReturn(new TypeList.ForLoadedType(firstConstructor.getParameterTypes()));
        assertThat(describe(firstConstructor), equalTo(equalMethod));
        MethodDescription equalMethodButName = mock(MethodDescription.class);
        when(equalMethodButName.getInternalName()).thenReturn(firstMethod.getName());
        when(equalMethodButName.getDeclaringElement()).thenReturn(new TypeDescription.ForLoadedType(firstConstructor.getDeclaringClass()));
        when(equalMethodButName.getReturnType()).thenReturn(new TypeDescription.ForLoadedType(void.class));
        when(equalMethodButName.getParameterTypes()).thenReturn(new TypeList.ForLoadedType(firstConstructor.getParameterTypes()));
        assertThat(describe(firstConstructor), not(equalTo(equalMethodButName)));
        MethodDescription equalMethodButReturnType = mock(MethodDescription.class);
        when(equalMethodButReturnType.getInternalName()).thenReturn(MethodDescription.CONSTRUCTOR_INTERNAL_NAME);
        when(equalMethodButReturnType.getDeclaringElement()).thenReturn(new TypeDescription.ForLoadedType(Object.class));
        when(equalMethodButReturnType.getReturnType()).thenReturn(new TypeDescription.ForLoadedType(void.class));
        when(equalMethodButReturnType.getParameterTypes()).thenReturn(new TypeList.ForLoadedType(firstConstructor.getParameterTypes()));
        assertThat(describe(firstConstructor), not(equalTo(equalMethodButReturnType)));
        MethodDescription equalMethodButDeclaringType = mock(MethodDescription.class);
        when(equalMethodButDeclaringType.getInternalName()).thenReturn(MethodDescription.CONSTRUCTOR_INTERNAL_NAME);
        when(equalMethodButDeclaringType.getDeclaringElement()).thenReturn(new TypeDescription.ForLoadedType(firstConstructor.getDeclaringClass()));
        when(equalMethodButDeclaringType.getReturnType()).thenReturn(new TypeDescription.ForLoadedType(Object.class));
        when(equalMethodButDeclaringType.getParameterTypes()).thenReturn(new TypeList.ForLoadedType(firstConstructor.getParameterTypes()));
        assertThat(describe(firstConstructor), not(equalTo(equalMethodButDeclaringType)));
        MethodDescription equalMethodButParameterTypes = mock(MethodDescription.class);
        when(equalMethodButParameterTypes.getInternalName()).thenReturn(MethodDescription.CONSTRUCTOR_INTERNAL_NAME);
        when(equalMethodButParameterTypes.getDeclaringElement()).thenReturn(new TypeDescription.ForLoadedType(firstConstructor.getDeclaringClass()));
        when(equalMethodButParameterTypes.getReturnType()).thenReturn(new TypeDescription.ForLoadedType(void.class));
        when(equalMethodButParameterTypes.getParameterTypes()).thenReturn(new TypeList.ForLoadedType(secondConstructor.getParameterTypes()));
        assertThat(describe(firstConstructor), not(equalTo(equalMethodButParameterTypes)));
        assertThat(describe(firstConstructor), not(equalTo(new Object())));
        assertThat(describe(firstConstructor), not(equalTo(null)));
    }

    @Test
    public void testToString() throws Exception {
        assertThat(describe(firstMethod).toString(), is(firstMethod.toString()));
        assertThat(describe(secondMethod).toString(), is(secondMethod.toString()));
        assertThat(describe(thirdMethod).toString(), is(thirdMethod.toString()));
        assertThat(describe(firstConstructor).toString(), is(firstConstructor.toString()));
        assertThat(describe(secondConstructor).toString(), is(secondConstructor.toString()));
    }

    @Test
    public void testSynthetic() throws Exception {
        assertThat(describe(firstMethod).isSynthetic(), is(firstMethod.isSynthetic()));
        assertThat(describe(secondMethod).isSynthetic(), is(secondMethod.isSynthetic()));
        assertThat(describe(thirdMethod).isSynthetic(), is(thirdMethod.isSynthetic()));
        assertThat(describe(firstConstructor).isSynthetic(), is(firstConstructor.isSynthetic()));
        assertThat(describe(secondConstructor).isSynthetic(), is(secondConstructor.isSynthetic()));
    }

    @Test
    public void testType() throws Exception {
        assertThat(describe(firstMethod).isMethod(), is(true));
        assertThat(describe(firstMethod).isConstructor(), is(false));
        assertThat(describe(firstMethod).isTypeInitializer(), is(false));
        assertThat(describe(firstConstructor).isMethod(), is(false));
        assertThat(describe(firstConstructor).isConstructor(), is(true));
        assertThat(describe(firstConstructor).isTypeInitializer(), is(false));
    }

    @Test
    public void testMethodIsVisibleTo() throws Exception {
        assertThat(describe(PublicType.class.getDeclaredMethod("publicMethod"))
                .isVisibleTo(new TypeDescription.ForLoadedType(PublicType.class)), is(true));
        assertThat(describe(PublicType.class.getDeclaredMethod("protectedMethod"))
                .isVisibleTo(new TypeDescription.ForLoadedType(PublicType.class)), is(true));
        assertThat(describe(PublicType.class.getDeclaredMethod("packagePrivateMethod"))
                .isVisibleTo(new TypeDescription.ForLoadedType(PublicType.class)), is(true));
        assertThat(describe(PublicType.class.getDeclaredMethod("privateMethod"))
                .isVisibleTo(new TypeDescription.ForLoadedType(PublicType.class)), is(true));
        assertThat(describe(PublicType.class.getDeclaredMethod("publicMethod"))
                .isVisibleTo(new TypeDescription.ForLoadedType(Sample.class)), is(true));
        assertThat(describe(PublicType.class.getDeclaredMethod("protectedMethod"))
                .isVisibleTo(new TypeDescription.ForLoadedType(Sample.class)), is(true));
        assertThat(describe(PublicType.class.getDeclaredMethod("packagePrivateMethod"))
                .isVisibleTo(new TypeDescription.ForLoadedType(Sample.class)), is(true));
        assertThat(describe(PublicType.class.getDeclaredMethod("privateMethod"))
                .isVisibleTo(new TypeDescription.ForLoadedType(Sample.class)), is(false));
        assertThat(describe(PublicType.class.getDeclaredMethod("publicMethod"))
                .isVisibleTo(new TypeDescription.ForLoadedType(Object.class)), is(true));
        assertThat(describe(PublicType.class.getDeclaredMethod("protectedMethod"))
                .isVisibleTo(new TypeDescription.ForLoadedType(Object.class)), is(false));
        assertThat(describe(PublicType.class.getDeclaredMethod("packagePrivateMethod"))
                .isVisibleTo(new TypeDescription.ForLoadedType(Object.class)), is(false));
        assertThat(describe(PublicType.class.getDeclaredMethod("privateMethod"))
                .isVisibleTo(new TypeDescription.ForLoadedType(Object.class)), is(false));
        assertThat(describe(PublicType.class.getDeclaredMethod("publicMethod"))
                .isVisibleTo(new TypeDescription.ForLoadedType(VisibilityMethodTestHelper.class)), is(true));
        assertThat(describe(PublicType.class.getDeclaredMethod("protectedMethod"))
                .isVisibleTo(new TypeDescription.ForLoadedType(VisibilityMethodTestHelper.class)), is(true));
        assertThat(describe(PublicType.class.getDeclaredMethod("packagePrivateMethod"))
                .isVisibleTo(new TypeDescription.ForLoadedType(VisibilityMethodTestHelper.class)), is(false));
        assertThat(describe(PublicType.class.getDeclaredMethod("privateMethod"))
                .isVisibleTo(new TypeDescription.ForLoadedType(VisibilityMethodTestHelper.class)), is(false));
        assertThat(describe(PackagePrivateType.class.getDeclaredMethod("publicMethod"))
                .isVisibleTo(new TypeDescription.ForLoadedType(Object.class)), is(false));
        assertThat(describe(PackagePrivateType.class.getDeclaredMethod("protectedMethod"))
                .isVisibleTo(new TypeDescription.ForLoadedType(Object.class)), is(false));
        assertThat(describe(PackagePrivateType.class.getDeclaredMethod("packagePrivateMethod"))
                .isVisibleTo(new TypeDescription.ForLoadedType(Object.class)), is(false));
        assertThat(describe(PackagePrivateType.class.getDeclaredMethod("privateMethod"))
                .isVisibleTo(new TypeDescription.ForLoadedType(Object.class)), is(false));
    }

    @Test
    public void testConstructorIsVisibleTo() throws Exception {
        assertThat(describe(PublicType.class.getDeclaredConstructor())
                .isVisibleTo(new TypeDescription.ForLoadedType(PublicType.class)), is(true));
        assertThat(describe(PublicType.class.getDeclaredConstructor(Void.class))
                .isVisibleTo(new TypeDescription.ForLoadedType(PublicType.class)), is(true));
        assertThat(describe(PublicType.class.getDeclaredConstructor(Object.class))
                .isVisibleTo(new TypeDescription.ForLoadedType(PublicType.class)), is(true));
        assertThat(describe(PublicType.class.getDeclaredConstructor(String.class))
                .isVisibleTo(new TypeDescription.ForLoadedType(PublicType.class)), is(true));
        assertThat(describe(PublicType.class.getDeclaredConstructor())
                .isVisibleTo(new TypeDescription.ForLoadedType(Sample.class)), is(true));
        assertThat(describe(PublicType.class.getDeclaredConstructor(Void.class))
                .isVisibleTo(new TypeDescription.ForLoadedType(Sample.class)), is(true));
        assertThat(describe(PublicType.class.getDeclaredConstructor(Object.class))
                .isVisibleTo(new TypeDescription.ForLoadedType(Sample.class)), is(true));
        assertThat(describe(PublicType.class.getDeclaredConstructor(String.class))
                .isVisibleTo(new TypeDescription.ForLoadedType(Sample.class)), is(false));
        assertThat(describe(PublicType.class.getDeclaredConstructor())
                .isVisibleTo(new TypeDescription.ForLoadedType(Object.class)), is(true));
        assertThat(describe(PublicType.class.getDeclaredConstructor(Void.class))
                .isVisibleTo(new TypeDescription.ForLoadedType(Object.class)), is(false));
        assertThat(describe(PublicType.class.getDeclaredConstructor(Object.class))
                .isVisibleTo(new TypeDescription.ForLoadedType(Object.class)), is(false));
        assertThat(describe(PublicType.class.getDeclaredConstructor(String.class))
                .isVisibleTo(new TypeDescription.ForLoadedType(Object.class)), is(false));
        assertThat(describe(PublicType.class.getDeclaredConstructor())
                .isVisibleTo(new TypeDescription.ForLoadedType(VisibilityMethodTestHelper.class)), is(true));
        assertThat(describe(PublicType.class.getDeclaredConstructor(Void.class))
                .isVisibleTo(new TypeDescription.ForLoadedType(VisibilityMethodTestHelper.class)), is(true));
        assertThat(describe(PublicType.class.getDeclaredConstructor(Object.class))
                .isVisibleTo(new TypeDescription.ForLoadedType(VisibilityMethodTestHelper.class)), is(false));
        assertThat(describe(PublicType.class.getDeclaredConstructor(String.class))
                .isVisibleTo(new TypeDescription.ForLoadedType(VisibilityMethodTestHelper.class)), is(false));
        assertThat(describe(PackagePrivateType.class.getDeclaredConstructor())
                .isVisibleTo(new TypeDescription.ForLoadedType(Object.class)), is(false));
        assertThat(describe(PackagePrivateType.class.getDeclaredConstructor(Void.class))
                .isVisibleTo(new TypeDescription.ForLoadedType(Object.class)), is(false));
        assertThat(describe(PackagePrivateType.class.getDeclaredConstructor(Object.class))
                .isVisibleTo(new TypeDescription.ForLoadedType(Object.class)), is(false));
        assertThat(describe(PackagePrivateType.class.getDeclaredConstructor(String.class))
                .isVisibleTo(new TypeDescription.ForLoadedType(Object.class)), is(false));
    }

    @Test
    public void testExceptions() throws Exception {
        assertThat(describe(firstMethod).getExceptionTypes(),
                is((TypeList) new TypeList.ForLoadedType(firstMethod.getExceptionTypes())));
        assertThat(describe(secondMethod).getExceptionTypes(),
                is((TypeList) new TypeList.ForLoadedType(secondMethod.getExceptionTypes())));
        assertThat(describe(thirdMethod).getExceptionTypes(),
                is((TypeList) new TypeList.ForLoadedType(thirdMethod.getExceptionTypes())));
        assertThat(describe(firstConstructor).getExceptionTypes(),
                is((TypeList) new TypeList.ForLoadedType(firstConstructor.getExceptionTypes())));
        assertThat(describe(secondConstructor).getExceptionTypes(),
                is((TypeList) new TypeList.ForLoadedType(secondConstructor.getExceptionTypes())));
    }

    @Test
    public void testAnnotations() throws Exception {
        assertThat(describe(firstMethod).getDeclaredAnnotations(), is((AnnotationList) new AnnotationList.Empty()));
        assertThat(describe(secondMethod).getDeclaredAnnotations(), is((AnnotationList) new AnnotationList.Empty()));
        assertThat(describe(thirdMethod).getDeclaredAnnotations(),
                is((AnnotationList) new AnnotationList.ForLoadedAnnotation(thirdMethod.getDeclaredAnnotations())));
        assertThat(describe(firstConstructor).getDeclaredAnnotations(), is((AnnotationList) new AnnotationList.Empty()));
        assertThat(describe(secondConstructor).getDeclaredAnnotations(),
                is((AnnotationList) new AnnotationList.ForLoadedAnnotation(secondConstructor.getDeclaredAnnotations())));
        assertThat(describe(firstMethod).getParameterAnnotations(), is(AnnotationList.Empty.asList(0)));
        assertThat(describe(secondMethod).getParameterAnnotations(), is(AnnotationList.Empty.asList(2)));
        assertThat(describe(thirdMethod).getParameterAnnotations(),
                is(AnnotationList.ForLoadedAnnotation.asList(thirdMethod.getParameterAnnotations())));
        assertThat(describe(firstConstructor).getParameterAnnotations(), is(AnnotationList.Empty.asList(1)));
        assertThat(describe(secondConstructor).getParameterAnnotations(),
                is(AnnotationList.ForLoadedAnnotation.asList(secondConstructor.getParameterAnnotations())));
    }

    @Test
    public void testRepresents() throws Exception {
        assertThat(describe(firstMethod).represents(firstMethod), is(true));
        assertThat(describe(firstMethod).represents(secondMethod), is(false));
        assertThat(describe(firstMethod).represents(thirdMethod), is(false));
        assertThat(describe(firstMethod).represents(firstConstructor), is(false));
        assertThat(describe(firstMethod).represents(secondConstructor), is(false));
        assertThat(describe(firstConstructor).represents(firstConstructor), is(true));
        assertThat(describe(firstConstructor).represents(secondConstructor), is(false));
        assertThat(describe(firstConstructor).represents(firstMethod), is(false));
        assertThat(describe(firstConstructor).represents(secondMethod), is(false));
        assertThat(describe(firstConstructor).represents(thirdMethod), is(false));
    }

    @Test
    public void testSpecializable() throws Exception {
        assertThat(describe(firstMethod).isSpecializableFor(new TypeDescription.ForLoadedType(Sample.class)), is(false));
        assertThat(describe(secondMethod).isSpecializableFor(new TypeDescription.ForLoadedType(Sample.class)), is(false));
        assertThat(describe(thirdMethod).isSpecializableFor(new TypeDescription.ForLoadedType(Sample.class)), is(true));
        assertThat(describe(thirdMethod).isSpecializableFor(new TypeDescription.ForLoadedType(SampleSub.class)), is(true));
        assertThat(describe(thirdMethod).isSpecializableFor(new TypeDescription.ForLoadedType(Object.class)), is(false));
        assertThat(describe(firstConstructor).isSpecializableFor(new TypeDescription.ForLoadedType(Sample.class)), is(true));
        assertThat(describe(firstConstructor).isSpecializableFor(new TypeDescription.ForLoadedType(SampleSub.class)), is(false));
    }

    @Retention(RetentionPolicy.RUNTIME)
    private static @interface SampleAnnotation {
    }

    private static abstract class Sample {

        Sample(Void argument) {

        }

        @SampleAnnotation
        private Sample(int[] first, @SampleAnnotation long second) throws IOException {

        }

        private static void first() {
            /* do nothing */
        }

        protected abstract Object second(String first, long second) throws RuntimeException, IOException;

        @SampleAnnotation
        public boolean[] third(@SampleAnnotation Object[] third, int[] forth) throws Throwable {
            return null;
        }
    }

    private static abstract class SampleSub extends Sample {

        protected SampleSub(Void argument) {
            super(argument);
        }
    }

    public static abstract class PublicType {

        public PublicType() {
            /* do nothing*/
        }

        protected PublicType(Void protectedConstructor) {
            /* do nothing*/
        }

        PublicType(Object packagePrivateConstructor) {
            /* do nothing*/
        }

        private PublicType(String privateConstructor) {
            /* do nothing*/
        }

        public abstract void publicMethod();

        protected abstract void protectedMethod();

        abstract void packagePrivateMethod();

        private void privateMethod() {
            /* do nothing*/
        }
    }

    static abstract class PackagePrivateType {

        public PackagePrivateType() {
            /* do nothing*/
        }

        protected PackagePrivateType(Void protectedConstructor) {
            /* do nothing*/
        }

        PackagePrivateType(Object packagePrivateConstructor) {
            /* do nothing*/
        }

        private PackagePrivateType(String privateConstructor) {
            /* do nothing*/
        }

        public abstract void publicMethod();

        protected abstract void protectedMethod();

        abstract void packagePrivateMethod();

        private void privateMethod() {
            /* do nothing*/
        }
    }
}

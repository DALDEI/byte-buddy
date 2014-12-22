package net.bytebuddy.instrumentation.method;

import net.bytebuddy.instrumentation.ByteCodeElement;
import net.bytebuddy.instrumentation.attribute.annotation.AnnotationDescription;
import net.bytebuddy.instrumentation.attribute.annotation.AnnotationList;
import net.bytebuddy.instrumentation.type.TypeDescription;
import net.bytebuddy.instrumentation.type.TypeList;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;

/**
 * Implementations of this interface describe a Java method, i.e. a method or a constructor. Implementations of this
 * interface must provide meaningful {@code equal(Object)} and {@code hashCode()} implementations.
 */
public interface MethodDescription extends ByteCodeElement {

    /**
     * The internal name of a Java constructor.
     */
    static final String CONSTRUCTOR_INTERNAL_NAME = "<init>";

    /**
     * The internal name of a Java static initializer.
     */
    static final String TYPE_INITIALIZER_INTERNAL_NAME = "<clinit>";

    /**
     * The type initializer of any representation of a type initializer.
     */
    static final int TYPE_INITIALIZER_MODIFIER = Opcodes.ACC_STATIC | Opcodes.ACC_PRIVATE | Opcodes.ACC_SYNTHETIC;

    /**
     * Returns a description of the return type of the method described by this instance.
     *
     * @return A description of the return type of the method described by this instance.
     */
    TypeDescription getReturnType();

    /**
     * Returns a list of type descriptions of the method described by this instance.
     *
     * @return A list of type descriptions of the method described by this instance.
     */
    TypeList getParameterTypes();

    /**
     * Returns the parameter annotations of the method described by this instance.
     *
     * @return The parameter annotations of the method described by this instance.
     */
    List<AnnotationList> getParameterAnnotations();

    /**
     * Returns a description of the exception types of the method described by this instance.
     *
     * @return A description of the exception types of the method described by this instance.
     */
    TypeList getExceptionTypes();

    /**
     * Returns this method modifier but adjusts its state of being abstract.
     *
     * @param nonAbstract {@code true} if the method should be treated as non-abstract.
     * @return The adjusted modifiers.
     */
    int getAdjustedModifiers(boolean nonAbstract);

    /**
     * Checks if this method description represents a constructor.
     *
     * @return {@code true} if this method description represents a constructor.
     */
    boolean isConstructor();

    /**
     * Checks if this method description represents a method, i.e. not a constructor or a type initializer.
     *
     * @return {@code true} if this method description represents a method.
     */
    boolean isMethod();

    /**
     * Checks if this method is a type initializer.
     *
     * @return {@code true} if this method description represents a type initializer.
     */
    boolean isTypeInitializer();

    /**
     * Verifies if a method description represents a given loaded method.
     *
     * @param method The method to be checked.
     * @return {@code true} if this method description represents the given loaded method.
     */
    boolean represents(Method method);

    /**
     * Verifies if a method description represents a given loaded constructor.
     *
     * @param constructor The constructor to be checked.
     * @return {@code true} if this method description represents the given loaded constructor.
     */
    boolean represents(Constructor<?> constructor);

    /**
     * Verifies if this method description represents an overridable method.
     *
     * @return {@code true} if this method description represents an overridable method.
     */
    boolean isOverridable();

    /**
     * Returns the size of the local variable array that is required for this method, i.e. the size of all parameters
     * if they were loaded on the stack including a reference to {@code this} if this method represented a non-static
     * method.
     *
     * @return The size of this method on the operand stack.
     */
    int getStackSize();

    /**
     * Returns the offset of the parameter at {@code parameterIndex} on the described method's local variable array.
     *
     * @param parameterIndex The parameter index of interest.
     * @return The offset of this parameter.
     */
    int getParameterOffset(int parameterIndex);

    /**
     * Checks if this method represents a Java 8+ default method.
     *
     * @return {@code true} if this method is a default method.
     */
    boolean isDefaultMethod();

    /**
     * Checks if this method can be called using the {@code INVOKESPECIAL} for a given type.
     *
     * @param typeDescription The type o
     * @return {@code true} if this method can be called using the {@code INVOKESPECIAL} instruction
     * using the given type.
     */
    boolean isSpecializableFor(TypeDescription typeDescription);

    /**
     * Returns the unique signature of a byte code method. A unique signature is defined as the concatenation of
     * the internal name of the method / constructor and the method descriptor. Note that methods on byte code
     * level do consider two similar methods with different return type as distinct methods.
     *
     * @return A unique signature of this byte code level method.
     */
    String getUniqueSignature();

    /**
     * Returns the default value of this method or {@code null} if no such value exists. The returned values might be
     * of a different type than usual:
     * <ul>
     * <li>{@link java.lang.Class} values are represented as
     * {@link net.bytebuddy.instrumentation.type.TypeDescription}s.</li>
     * <li>{@link java.lang.annotation.Annotation} values are represented as
     * {@link net.bytebuddy.instrumentation.attribute.annotation.AnnotationDescription}s</li>
     * <li>{@link java.lang.Enum} values are represented as
     * {@link net.bytebuddy.instrumentation.attribute.annotation.AnnotationDescription.EnumerationValue}s.</li>
     * <li>Arrays of the latter types are represented as arrays of the named wrapper types.</li>
     * </ul>
     *
     * @return The default value of this method or {@code null}.
     */
    Object getDefaultValue();

    /**
     * Returns the default value but casts it to the given type. If the type differs from the value, a
     * {@link java.lang.ClassCastException} is thrown.
     *
     * @param type The type to cast the default value to.
     * @param <T>  The type to cast the default value to.
     * @return The casted default value.
     */
    <T> T getDefaultValue(Class<T> type);

    /**
     * An abstract base implementation of a method description.
     */
    abstract static class AbstractMethodDescription extends AbstractModifierReviewable implements MethodDescription {

        /**
         * A merger of all method modifiers that are visible in the Java source code.
         */
        private static final int SOURCE_MODIFIERS = Modifier.PUBLIC
                | Modifier.PROTECTED
                | Modifier.PRIVATE
                | Modifier.ABSTRACT
                | Modifier.STATIC
                | Modifier.FINAL
                | Modifier.SYNCHRONIZED
                | Modifier.NATIVE;

        @Override
        public String getUniqueSignature() {
            return getInternalName() + getDescriptor();
        }

        @Override
        public int getStackSize() {
            return getParameterTypes().getStackSize() + (isStatic() ? 0 : 1);
        }

        @Override
        public boolean isMethod() {
            return !isConstructor() && !isTypeInitializer();
        }

        @Override
        public String getName() {
            return isMethod() ? getInternalName() : getDeclaringElement().getName();
        }

        @Override
        public String getSourceCodeName() {
            return isMethod() ? getName() : "";
        }

        @Override
        public String getDescriptor() {
            StringBuilder descriptor = new StringBuilder("(");
            for (TypeDescription parameterType : getParameterTypes()) {
                descriptor.append(parameterType.getDescriptor());
            }
            return descriptor.append(")").append(getReturnType().getDescriptor()).toString();
        }

        @Override
        public String getGenericSignature() {
            return null; // Currently, generic signatures are not supported.
        }

        @Override
        public int getAdjustedModifiers(boolean nonAbstract) {
            return nonAbstract
                    ? getModifiers() & ~(Opcodes.ACC_ABSTRACT | Opcodes.ACC_NATIVE)
                    : getModifiers() & ~Opcodes.ACC_NATIVE | Opcodes.ACC_ABSTRACT;
        }

        @Override
        public boolean isVisibleTo(TypeDescription typeDescription) {
            return getDeclaringElement().isVisibleTo(typeDescription)
                    && (isPublic()
                    || typeDescription.equals(getDeclaringElement())
                    || (isProtected() && getDeclaringElement().isAssignableFrom(typeDescription))
                    || (!isPrivate() && typeDescription.isSamePackage(getDeclaringElement())));
        }

        @Override
        public boolean isOverridable() {
            return !(isConstructor() || isFinal() || isPrivate() || isStatic());
        }

        @Override
        public int getParameterOffset(int parameterIndex) {
            int offset = isStatic() ? 0 : 1;
            int currentIndex = 0;
            for (TypeDescription parameterType : getParameterTypes()) {
                if (currentIndex == parameterIndex) {
                    return offset;
                } else {
                    currentIndex++;
                    offset += parameterType.getStackSize().getSize();
                }
            }
            throw new IllegalArgumentException(this + " does not have a parameter of index " + parameterIndex);
        }

        @Override
        public boolean isDefaultMethod() {
            return !isAbstract() && !isBridge() && getDeclaringElement().isInterface();
        }

        @Override
        public boolean isSpecializableFor(TypeDescription targetType) {
            if (isStatic()) { // Static private methods are never specializable, check static property first
                return false;
            } else if (isPrivate() || isConstructor() || isDefaultMethod()) {
                return getDeclaringElement().equals(targetType);
            } else {
                return !isAbstract() && getDeclaringElement().isAssignableFrom(targetType);
            }
        }

        @Override
        public <T> T getDefaultValue(Class<T> type) {
            return type.cast(getDefaultValue());
        }

        @Override
        public boolean equals(Object other) {
            return other == this || other instanceof MethodDescription
                    && getInternalName().equals(((MethodDescription) other).getInternalName())
                    && getDeclaringElement().equals(((MethodDescription) other).getDeclaringElement())
                    && getReturnType().equals(((MethodDescription) other).getReturnType())
                    && getParameterTypes().equals(((MethodDescription) other).getParameterTypes());
        }

        @Override
        public int hashCode() {
            int hashCode = getDeclaringElement().hashCode();
            hashCode = 31 * hashCode + getInternalName().hashCode();
            hashCode = 31 * hashCode + getReturnType().hashCode();
            return 31 * hashCode + getParameterTypes().hashCode();
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            int modifiers = getModifiers() & SOURCE_MODIFIERS;
            if (modifiers != 0) {
                stringBuilder.append(Modifier.toString(modifiers)).append(" ");
            }
            if (isMethod()) {
                stringBuilder.append(getReturnType().getSourceCodeName()).append(" ");
                stringBuilder.append(getDeclaringElement().getSourceCodeName()).append(".");
            }
            stringBuilder.append(getName()).append("(");
            boolean first = true;
            for (TypeDescription typeDescription : getParameterTypes()) {
                if (!first) {
                    stringBuilder.append(",");
                } else {
                    first = false;
                }
                stringBuilder.append(typeDescription.getSourceCodeName());
            }
            stringBuilder.append(")");
            TypeList exceptionTypes = getExceptionTypes();
            if (exceptionTypes.size() > 0) {
                stringBuilder.append(" throws ");
                first = true;
                for (TypeDescription typeDescription : exceptionTypes) {
                    if (!first) {
                        stringBuilder.append(",");
                    } else {
                        first = false;
                    }
                    stringBuilder.append(typeDescription.getName());
                }
            }
            return stringBuilder.toString();
        }
    }

    /**
     * An implementation of a method description for a loaded constructor.
     */
    static class ForLoadedConstructor extends AbstractMethodDescription {

        /**
         * The loaded constructor that is represented by this instance.
         */
        private final Constructor<?> constructor;

        /**
         * Creates a new immutable method description for a loaded constructor.
         *
         * @param constructor The loaded constructor to be represented by this method description.
         */
        public ForLoadedConstructor(Constructor<?> constructor) {
            this.constructor = constructor;
        }

        @Override
        public TypeDescription getDeclaringElement() {
            return new TypeDescription.ForLoadedType(constructor.getDeclaringClass());
        }

        @Override
        public TypeDescription getReturnType() {
            return new TypeDescription.ForLoadedType(void.class);
        }

        @Override
        public TypeList getParameterTypes() {
            return new TypeList.ForLoadedType(constructor.getParameterTypes());
        }

        @Override
        public List<AnnotationList> getParameterAnnotations() {
            return AnnotationList.ForLoadedAnnotation.asList(constructor.getParameterAnnotations());
        }

        @Override
        public TypeList getExceptionTypes() {
            return new TypeList.ForLoadedType(constructor.getExceptionTypes());
        }

        @Override
        public boolean isConstructor() {
            return true;
        }

        @Override
        public boolean isTypeInitializer() {
            return false;
        }

        @Override
        public boolean represents(Method method) {
            return false;
        }

        @Override
        public boolean represents(Constructor<?> constructor) {
            return this.constructor.equals(constructor);
        }

        @Override
        public String getName() {
            return constructor.getName();
        }

        @Override
        public int getModifiers() {
            return constructor.getModifiers();
        }

        @Override
        public boolean isSynthetic() {
            return constructor.isSynthetic();
        }

        @Override
        public String getInternalName() {
            return CONSTRUCTOR_INTERNAL_NAME;
        }

        @Override
        public String getDescriptor() {
            return Type.getConstructorDescriptor(constructor);
        }

        @Override
        public Object getDefaultValue() {
            return null;
        }

        @Override
        public AnnotationList getDeclaredAnnotations() {
            return new AnnotationList.ForLoadedAnnotation(constructor.getDeclaredAnnotations());
        }
    }

    /**
     * An implementation of a method description for a loaded method.
     */
    static class ForLoadedMethod extends AbstractMethodDescription {

        /**
         * The loaded method that is represented by this instance.
         */
        private final Method method;

        /**
         * Creates a new immutable method description for a loaded method.
         *
         * @param method The loaded method to be represented by this method description.
         */
        public ForLoadedMethod(Method method) {
            this.method = method;
        }

        @Override
        public TypeDescription getDeclaringElement() {
            return new TypeDescription.ForLoadedType(method.getDeclaringClass());
        }

        @Override
        public TypeDescription getReturnType() {
            return new TypeDescription.ForLoadedType(method.getReturnType());
        }

        @Override
        public TypeList getParameterTypes() {
            return new TypeList.ForLoadedType(method.getParameterTypes());
        }

        @Override
        public List<AnnotationList> getParameterAnnotations() {
            return AnnotationList.ForLoadedAnnotation.asList(method.getParameterAnnotations());
        }

        @Override
        public TypeList getExceptionTypes() {
            return new TypeList.ForLoadedType(method.getExceptionTypes());
        }

        @Override
        public boolean isConstructor() {
            return false;
        }

        @Override
        public boolean isTypeInitializer() {
            return false;
        }

        @Override
        public boolean isBridge() {
            return method.isBridge();
        }

        @Override
        public boolean represents(Method method) {
            return this.method.equals(method);
        }

        @Override
        public boolean represents(Constructor<?> constructor) {
            return false;
        }

        @Override
        public String getName() {
            return method.getName();
        }

        @Override
        public int getModifiers() {
            return method.getModifiers();
        }

        @Override
        public boolean isSynthetic() {
            return method.isSynthetic();
        }

        @Override
        public String getInternalName() {
            return method.getName();
        }

        @Override
        public String getDescriptor() {
            return Type.getMethodDescriptor(method);
        }

        /**
         * Returns the loaded method that is represented by this method description.
         *
         * @return The loaded method that is represented by this method description.
         */
        public Method getLoadedMethod() {
            return method;
        }

        @Override
        public AnnotationList getDeclaredAnnotations() {
            return new AnnotationList.ForLoadedAnnotation(method.getDeclaredAnnotations());
        }

        @Override
        public Object getDefaultValue() {
            Object value = method.getDefaultValue();
            return value == null
                    ? null
                    : AnnotationDescription.ForLoadedAnnotation.wrap(value, new TypeDescription.ForLoadedType(method.getReturnType()));
        }
    }

    /**
     * A latent method description describes a method that is not attached to a declaring
     * {@link net.bytebuddy.instrumentation.type.TypeDescription}.
     */
    static class Latent extends AbstractMethodDescription {

        /**
         * the internal name of this method.
         */
        private final String internalName;

        /**
         * The type that is declaring this method.
         */
        private final TypeDescription declaringType;

        /**
         * The return type of this method.
         */
        private final TypeDescription returnType;

        /**
         * The parameter types of this methods.
         */
        private final List<? extends TypeDescription> parameterTypes;

        /**
         * The modifiers of this method.
         */
        private final int modifiers;

        /**
         * This method's exception types.
         */
        private final List<? extends TypeDescription> exceptionTypes;

        /**
         * Creates an immutable latent method description.
         *
         * @param internalName   The internal name of the method.
         * @param declaringType  The type that is declaring this method latently.
         * @param returnType     The return type of this method.
         * @param parameterTypes The parameter types of this method.
         * @param modifiers      The modifiers of this method.
         * @param exceptionTypes The exception types of this method.
         */
        public Latent(String internalName,
                      TypeDescription declaringType,
                      TypeDescription returnType,
                      List<? extends TypeDescription> parameterTypes,
                      int modifiers,
                      List<? extends TypeDescription> exceptionTypes) {
            this.internalName = internalName;
            this.declaringType = declaringType;
            this.returnType = returnType;
            this.parameterTypes = parameterTypes;
            this.modifiers = modifiers;
            this.exceptionTypes = exceptionTypes;
        }

        /**
         * Creates a latent method description of a type initializer ({@code &lt;clinit&gt;}) for a given type.
         *
         * @param declaringType The type that for which a type initializer should be created.
         * @return A method description of the type initializer of the given type.
         */
        public static MethodDescription typeInitializerOf(TypeDescription declaringType) {
            return new Latent(MethodDescription.TYPE_INITIALIZER_INTERNAL_NAME,
                    declaringType,
                    new TypeDescription.ForLoadedType(void.class),
                    new TypeList.Empty(),
                    TYPE_INITIALIZER_MODIFIER,
                    Collections.<TypeDescription>emptyList());
        }

        @Override
        public TypeDescription getReturnType() {
            return returnType;
        }

        @Override
        public TypeList getParameterTypes() {
            return new TypeList.Explicit(parameterTypes);
        }

        @Override
        public List<AnnotationList> getParameterAnnotations() {
            return AnnotationList.Empty.asList(parameterTypes.size());
        }

        @Override
        public TypeList getExceptionTypes() {
            return new TypeList.Explicit(exceptionTypes);
        }

        @Override
        public boolean isConstructor() {
            return CONSTRUCTOR_INTERNAL_NAME.equals(internalName);
        }

        @Override
        public boolean isTypeInitializer() {
            return TYPE_INITIALIZER_INTERNAL_NAME.equals(internalName);
        }

        @Override
        public boolean represents(Method method) {
            return equals(new ForLoadedMethod(method));
        }

        @Override
        public boolean represents(Constructor<?> constructor) {
            return equals(new ForLoadedConstructor(constructor));
        }

        @Override
        public AnnotationList getDeclaredAnnotations() {
            return new AnnotationList.Empty();
        }

        @Override
        public String getInternalName() {
            return internalName;
        }

        @Override
        public TypeDescription getDeclaringElement() {
            return declaringType;
        }

        @Override
        public int getModifiers() {
            return modifiers;
        }

        @Override
        public Object getDefaultValue() {
            return null;
        }
    }
}

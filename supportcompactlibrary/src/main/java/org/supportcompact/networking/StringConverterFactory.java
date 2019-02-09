package org.supportcompact.networking;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import io.reactivex.annotations.Nullable;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Converter;
import retrofit2.Retrofit;


/**
 * A {@linkplain Converter.Factory converter} which uses Simple Framework for XML.
 * <p>
 * This converter only applies for class types. Parameterized types (e.g., {@code List<Foo>}) are
 * not handled.
 *
 * @deprecated we recommend switching to the JAXB converter.
 */
public final class StringConverterFactory extends Converter.Factory {
    /**
     * Create an instance using a default {@link Persister} instance for conversion.
     */
    public static StringConverterFactory create() {
        return create(new Persister());
    }

    /**
     * Create an instance using {@code serializer} for conversion.
     */
    public static StringConverterFactory create(Serializer serializer) {
        return new StringConverterFactory(serializer, true);
    }

    /**
     * Create an instance using a default {@link Persister} instance for non-strict conversion.
     */
    public static StringConverterFactory createNonStrict() {
        return createNonStrict(new Persister());
    }

    /**
     * Create an instance using {@code serializer} for non-strict conversion.
     */
    @SuppressWarnings("ConstantConditions") // Guarding public API nullability.
    public static StringConverterFactory createNonStrict(Serializer serializer) {
        if (serializer == null) throw new NullPointerException("serializer == null");
        return new StringConverterFactory(serializer, false);
    }

    private final Serializer serializer;
    private final boolean strict;

    private StringConverterFactory(Serializer serializer, boolean strict) {
        this.serializer = serializer;
        this.strict = strict;
    }

    public boolean isStrict() {
        return strict;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if (String.class.equals(type)) {
            return (Converter<ResponseBody, String>) ResponseBody::string;
        }
        return null;
    }

    @Override
    public @Nullable
    Converter<?, RequestBody> requestBodyConverter(Type type,
                                                   Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        if (!(type instanceof Class)) {
            return null;
        }
        return new SimpleXmlRequestBodyConverterB<>(serializer);
    }
}


final class SimpleXmlResponseBodyConverterA<T> implements Converter<ResponseBody, T> {
    private final Class<T> cls;
    private final Serializer serializer;
    private final boolean strict;

    SimpleXmlResponseBodyConverterA(Class<T> cls, Serializer serializer, boolean strict) {
        this.cls = cls;
        this.serializer = serializer;
        this.strict = strict;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        try {
            T read = serializer.read(cls, value.charStream(), strict);
            if (read == null) {
                throw new IllegalStateException("Could not deserialize body as " + cls);
            }
            return read;
        } catch (RuntimeException | IOException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            value.close();
        }
    }
}

final class SimpleXmlRequestBodyConverterB<T> implements Converter<T, RequestBody> {
    private static final MediaType MEDIA_TYPE = MediaType.get("application/xml; charset=UTF-8");
    private static final String CHARSET = "UTF-8";

    private final Serializer serializer;

    SimpleXmlRequestBodyConverterB(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public RequestBody convert(T value) throws IOException {
        Buffer buffer = new Buffer();
        try {
            OutputStreamWriter osw = new OutputStreamWriter(buffer.outputStream(), CHARSET);
            serializer.write(value, osw);
            osw.flush();
        } catch (RuntimeException | IOException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return RequestBody.create(MEDIA_TYPE, buffer.readByteString());
    }
}

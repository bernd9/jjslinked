package one.xis.sql.api;

import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;

@UtilityClass
public class NonComplexMapper {
    // TODO move to util class
    public byte toByte(byte v) {
        return v;
    }

    public byte toByte(short v) {
        return toByte(Short.toString(v));
    }

    public byte toByte(int v) {
        return toByte(Integer.toString(v));
    }

    public byte toByte(long v) {
        return toByte(Long.toString(v));
    }
    public byte toByte(float v) {
        return toByte(Float.toString(v));
    }
    public byte toByte(double v) {
        return toByte(Double.toString(v));
    }
    public byte toByte(char v) {
        return toByte(Integer.toString(v));
    }

    public byte toByte(Byte v) {
        if (v == null) throw new FailedConversionException(null, byte.class);
        return v;
    }

    public byte toByte(Short v) {
        if (v == null) throw new FailedConversionException(null, byte.class);
        return toByte(Short.toString(v));
    }

    public byte toByte(Integer v) {
        if (v == null) throw new FailedConversionException(null, byte.class);
        return toByte(Integer.toString(v));
    }

    public byte toByte(Long v) {
        if (v == null) throw new FailedConversionException(null, byte.class);
        return toByte(Long.toString(v));
    }
    public byte toByte(Float v) {
        if (v == null) throw new FailedConversionException(null, byte.class);
        return toByte(Float.toString(v));
    }
    public byte toByte(Double v) {
        if (v == null) throw new FailedConversionException(null, byte.class);
        return toByte(Double.toString(v));
    }
    public byte toByte(Character v) {
        if (v == null) throw new FailedConversionException(null, byte.class);
        return toByte(Integer.toString(v));
    }

    public byte toByte(String v) {
        if (v == null) throw new FailedConversionException(null, byte.class);
        byte[] bytes = v.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > 1 ) {
            throw new FailedConversionException(v,  byte.class);
        }
        return bytes[0];
    }


    public Byte toByteObj(byte v) {
        return v;
    }

    public Byte toByteObj(short v) {
        return toByte(Short.toString(v));
    }

    public Byte toByteObj(int v) {
        return toByte(Integer.toString(v));
    }

    public Byte toByteObj(long v) {
        return toByte(Long.toString(v));
    }
    public Byte toByteObj(float v) {
        return toByte(Float.toString(v));
    }
    public Byte toByteObj(double v) {
        return toByte(Double.toString(v));
    }
    public Byte toByteObj(char v) {
        return toByte(Integer.toString(v));
    }

    public Byte toByteObj(Byte v) {
        return v;
    }

    public Byte toByteObj(Short v) {
        if (v == null) return null;
        return toByte(Short.toString(v));
    }

    public Byte toByteObj(Integer v) {
        if (v == null) return null;
        return toByte(Integer.toString(v));
    }

    public Byte toByteObj(Long v) {
        if (v == null) return null;
        return toByte(Long.toString(v));
    }
    public Byte toByteObj(Float v) {
        if (v == null) return null;
        return toByte(Float.toString(v));
    }
    public Byte toByteObj(Double v) {
        if (v == null) return null;
        return toByte(Double.toString(v));
    }
    public Byte toByteObj(Character v) {
        if (v == null) return null;
        return toByte(Integer.toString(v));
    }

 
}

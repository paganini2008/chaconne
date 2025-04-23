package com.github.chaconne.cluster.utils;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.core.convert.support.DefaultConversionService;
import com.github.chaconne.utils.EnumConstant;
import com.github.chaconne.utils.EnumUtils;

/**
 * 
 * @Description: ConvertUtils
 * @Author: Fred Feng
 * @Date: 13/04/2025
 * @Version 1.0.0
 */
public abstract class ConvertUtils {

    private static final String[] SUPPORTED_DATE_TIME_PATTERNS = {"yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss.S", "yyyy-MM-dd'T'HH:mm:ss.SXXX", "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd.HH:mm:ss", "yyyy-MM-dd", "dd/MM/yyyy HH:mm:ss",
            "dd/MM/yyyy", "dd/MMM/yyyy", "yyyyMMddHHmmss", "yyyyMMdd", "yyyy-MM-dd HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "yyyy-MM-dd HH:mm:ss Z"};

    public static final LocalDate2DateConverterFactory LOCAL_DATE_TO_DATE =
            new LocalDate2DateConverterFactory();
    public static final LocalTime2DateConverterFactory LOCAL_TIME_TO_DATE =
            new LocalTime2DateConverterFactory();
    public static final LocalDateTime2DateConverterFactory LOCAL_DATE_TIME_TO_DATE =
            new LocalDateTime2DateConverterFactory();
    public static final Long2DateConverterFactory LONG_TO_DATE = new Long2DateConverterFactory();
    public static final String2DateConverterFactory STRING_TO_DATE =
            new String2DateConverterFactory();
    public static final Date2LocalDateConverterFactory DATE_TO_LOCAL_DATE =
            new Date2LocalDateConverterFactory();
    public static final Date2LocalDateTimeConverterFactory DATE_TO_LOCAL_DATE_TIME =
            new Date2LocalDateTimeConverterFactory();
    public static final Date2LocalTimeConverterFactory DATE_TO_LOCAL_TIME =
            new Date2LocalTimeConverterFactory();
    public static final String2LocalDateConverterFactory STRING_TO_LOCAL_DATE =
            new String2LocalDateConverterFactory();
    public static final String2LocalDateTimeConverterFactory STRING_TO_LOCAL_DATE_TIME =
            new String2LocalDateTimeConverterFactory();
    public static final String2LocalTimeConverterFactory STRING_TO_LOCAL_TIME =
            new String2LocalTimeConverterFactory();
    public static final Long2LocalDateConverterFactory LONG_TO_LOCAL_DATE =
            new Long2LocalDateConverterFactory();
    public static final Long2LocalDateTimeConverterFactory LONG_TO_LOCAL_DATE_TIME =
            new Long2LocalDateTimeConverterFactory();
    public static final Long2LocalTimeConverterFactory LONG_TO_LOCAL_TIME =
            new Long2LocalTimeConverterFactory();
    public static final LocalDate2StringConverterFactory LOCAL_DATE_TO_STIRNG =
            new LocalDate2StringConverterFactory();
    public static final LocalDateTime2StringConverterFactory LOCAL_DATE_TIME_TO_STIRNG =
            new LocalDateTime2StringConverterFactory();
    public static final LocalDateTime2LongConverterFactory LOCAL_DATE_TIME_TO_LONG =
            new LocalDateTime2LongConverterFactory();
    public static final EnumConstantConverterFactory OBJECT_TO_ENUM_CONSTANT =
            new EnumConstantConverterFactory();

    private static final DefaultConversionService conversionService =
            new DefaultConversionService();

    static {
        applyDefaultSettings(conversionService);
    }

    public static void applyDefaultSettings(DefaultConversionService conversionService) {
        conversionService.removeConvertible(String.class, Enum.class);
        conversionService.addConverterFactory(LOCAL_DATE_TO_DATE);
        conversionService.addConverterFactory(LOCAL_TIME_TO_DATE);
        conversionService.addConverterFactory(LOCAL_DATE_TIME_TO_DATE);
        conversionService.addConverterFactory(LONG_TO_DATE);
        conversionService.addConverterFactory(STRING_TO_DATE);
        conversionService.addConverterFactory(DATE_TO_LOCAL_DATE);
        conversionService.addConverterFactory(DATE_TO_LOCAL_DATE_TIME);
        conversionService.addConverterFactory(DATE_TO_LOCAL_TIME);
        conversionService.addConverterFactory(STRING_TO_LOCAL_DATE);
        conversionService.addConverterFactory(STRING_TO_LOCAL_DATE_TIME);
        conversionService.addConverterFactory(STRING_TO_LOCAL_TIME);
        conversionService.addConverterFactory(LONG_TO_LOCAL_DATE);
        conversionService.addConverterFactory(LONG_TO_LOCAL_DATE_TIME);
        conversionService.addConverterFactory(LONG_TO_LOCAL_TIME);
        conversionService.addConverterFactory(OBJECT_TO_ENUM_CONSTANT);
        conversionService.addConverterFactory(LOCAL_DATE_TO_STIRNG);
        conversionService.addConverterFactory(LOCAL_DATE_TIME_TO_STIRNG);
        conversionService.addConverterFactory(LOCAL_DATE_TIME_TO_LONG);

    }

    public static <S, R> void registerConverter(Converter<S, R> converter) {
        if (converter != null) {
            conversionService.addConverter(converter);
        }
    }

    public static <S, R> void registerConverterFactory(ConverterFactory<S, R> converterFactory) {
        if (converterFactory != null) {
            conversionService.addConverterFactory(converterFactory);
        }
    }

    public static ConversionService getDefaultConversionService() {
        return conversionService;
    }

    public static <T> T convert(Object source, Class<T> targetType) {
        if (source == null) {
            return null;
        }
        try {
            return targetType.cast(source);
        } catch (RuntimeException e) {
            return conversionService.convert(source, targetType);
        }
    }



    public static class EnumConstantConverterFactory
            implements ConverterFactory<String, EnumConstant> {

        @Override
        public <T extends EnumConstant> Converter<String, T> getConverter(Class<T> targetType) {
            return source -> {
                if (source == null) {
                    return null;
                }
                try {
                    return EnumUtils.valueOf(targetType, source);
                } catch (RuntimeException ignored) {
                    return null;
                }
            };
        }
    }

    public static class LocalDate2DateConverterFactory
            implements ConverterFactory<LocalDate, Date> {

        private final ZoneId zoneId;

        public LocalDate2DateConverterFactory() {
            this(ZoneId.systemDefault());
        }

        public LocalDate2DateConverterFactory(ZoneId zoneId) {
            this.zoneId = zoneId;
        }

        @Override
        public <T extends Date> Converter<LocalDate, T> getConverter(Class<T> targetType) {
            return source -> targetType.cast(Date.from(source.atStartOfDay(zoneId).toInstant()));
        }
    }

    public static class LocalTime2DateConverterFactory
            implements ConverterFactory<LocalTime, Date> {

        private final ZoneId zoneId;

        public LocalTime2DateConverterFactory() {
            this(ZoneId.systemDefault());
        }

        public LocalTime2DateConverterFactory(ZoneId zoneId) {
            this.zoneId = zoneId;
        }

        @Override
        public <T extends Date> Converter<LocalTime, T> getConverter(Class<T> targetType) {
            return source -> targetType
                    .cast(Date.from(source.atDate(LocalDate.now()).atZone(zoneId).toInstant()));
        }
    }

    public static class LocalDateTime2DateConverterFactory
            implements ConverterFactory<LocalDateTime, Date> {

        private final ZoneId zoneId;

        public LocalDateTime2DateConverterFactory() {
            this(ZoneId.systemDefault());
        }

        public LocalDateTime2DateConverterFactory(ZoneId zoneId) {
            this.zoneId = zoneId;
        }

        @Override
        public <T extends Date> Converter<LocalDateTime, T> getConverter(Class<T> targetType) {
            return source -> targetType.cast(Date.from(source.atZone(zoneId).toInstant()));
        }
    }

    public static class Long2DateConverterFactory implements ConverterFactory<Long, Date> {

        @Override
        public <T extends Date> Converter<Long, T> getConverter(Class<T> targetType) {
            return source -> targetType.cast(new Date(source));
        }
    }

    public static class String2DateConverterFactory implements ConverterFactory<String, Date> {

        @Override
        public <T extends Date> Converter<String, T> getConverter(Class<T> targetType) {
            return source -> {
                try {
                    return targetType.cast(DateUtils.parseDate(source, Locale.ENGLISH,
                            SUPPORTED_DATE_TIME_PATTERNS));
                } catch (ParseException e) {
                    throw new DateConversionException(e.getMessage(), e);
                }
            };
        }
    }

    public static class Date2LocalDateConverterFactory
            implements ConverterFactory<Date, LocalDate> {

        private final ZoneId zoneId;

        public Date2LocalDateConverterFactory() {
            this(ZoneId.systemDefault());
        }

        public Date2LocalDateConverterFactory(ZoneId zoneId) {
            this.zoneId = zoneId;
        }

        @Override
        public <T extends LocalDate> Converter<Date, T> getConverter(Class<T> targetType) {
            return source -> targetType.cast(source.toInstant().atZone(zoneId).toLocalDate());
        }
    }

    public static class Date2LocalDateTimeConverterFactory
            implements ConverterFactory<Date, LocalDateTime> {

        private final ZoneId zoneId;

        public Date2LocalDateTimeConverterFactory() {
            this(ZoneId.systemDefault());
        }

        public Date2LocalDateTimeConverterFactory(ZoneId zoneId) {
            this.zoneId = zoneId;
        }

        @Override
        public <T extends LocalDateTime> Converter<Date, T> getConverter(Class<T> targetType) {
            return source -> targetType.cast(source.toInstant().atZone(zoneId).toLocalDateTime());
        }
    }

    public static class Date2LocalTimeConverterFactory
            implements ConverterFactory<Date, LocalTime> {

        private final ZoneId zoneId;

        public Date2LocalTimeConverterFactory() {
            this(ZoneId.systemDefault());
        }

        public Date2LocalTimeConverterFactory(ZoneId zoneId) {
            this.zoneId = zoneId;
        }

        @Override
        public <T extends LocalTime> Converter<Date, T> getConverter(Class<T> targetType) {
            return source -> targetType.cast(source.toInstant().atZone(zoneId).toLocalTime());
        }
    }

    public static class Long2LocalDateConverterFactory
            implements ConverterFactory<Long, LocalDate> {

        private final ZoneId zoneId;

        public Long2LocalDateConverterFactory() {
            this(ZoneId.systemDefault());
        }

        public Long2LocalDateConverterFactory(ZoneId zoneId) {
            this.zoneId = zoneId;
        }

        @Override
        public <T extends LocalDate> Converter<Long, T> getConverter(Class<T> targetType) {
            return source -> targetType
                    .cast(Instant.ofEpochMilli(source).atZone(zoneId).toLocalDate());
        }
    }

    public static class Long2LocalDateTimeConverterFactory
            implements ConverterFactory<Long, LocalDateTime> {

        private final ZoneId zoneId;

        public Long2LocalDateTimeConverterFactory() {
            this(ZoneId.systemDefault());
        }

        public Long2LocalDateTimeConverterFactory(ZoneId zoneId) {
            this.zoneId = zoneId;
        }

        @Override
        public <T extends LocalDateTime> Converter<Long, T> getConverter(Class<T> targetType) {
            return source -> targetType
                    .cast(Instant.ofEpochMilli(source).atZone(zoneId).toLocalDateTime());
        }
    }

    public static class Long2LocalTimeConverterFactory
            implements ConverterFactory<Long, LocalTime> {

        private final ZoneId zoneId;

        public Long2LocalTimeConverterFactory() {
            this(ZoneId.systemDefault());
        }

        public Long2LocalTimeConverterFactory(ZoneId zoneId) {
            this.zoneId = zoneId;
        }

        @Override
        public <T extends LocalTime> Converter<Long, T> getConverter(Class<T> targetType) {
            return source -> {
                return targetType.cast(Instant.ofEpochMilli(source).atZone(zoneId).toLocalTime());
            };
        }
    }

    public static class String2LocalDateConverterFactory
            implements ConverterFactory<String, LocalDate> {

        private final ZoneId zoneId;

        public String2LocalDateConverterFactory() {
            this(ZoneId.systemDefault());
        }

        public String2LocalDateConverterFactory(ZoneId zoneId) {
            this.zoneId = zoneId;
        }

        @Override
        public <T extends LocalDate> Converter<String, T> getConverter(Class<T> targetType) {
            return source -> {
                Date date;
                try {
                    date = (Date) (DateUtils.parseDate(source, Locale.ENGLISH,
                            SUPPORTED_DATE_TIME_PATTERNS));
                } catch (ParseException e) {
                    throw new DateConversionException(e.getMessage(), e);
                }
                return targetType.cast(date.toInstant().atZone(zoneId).toLocalDate());
            };
        }
    }

    public static class String2LocalDateTimeConverterFactory
            implements ConverterFactory<String, LocalDateTime> {

        private final ZoneId zoneId;

        public String2LocalDateTimeConverterFactory() {
            this(ZoneId.systemDefault());
        }

        public String2LocalDateTimeConverterFactory(ZoneId zoneId) {
            this.zoneId = zoneId;
        }

        @Override
        public <T extends LocalDateTime> Converter<String, T> getConverter(Class<T> targetType) {
            return source -> {
                Date date;
                try {
                    date = (Date) (DateUtils.parseDate(source, Locale.ENGLISH,
                            SUPPORTED_DATE_TIME_PATTERNS));
                } catch (ParseException e) {
                    throw new DateConversionException(e.getMessage(), e);
                }
                return targetType.cast(date.toInstant().atZone(zoneId).toLocalDateTime());
            };
        }
    }

    public static class String2LocalTimeConverterFactory
            implements ConverterFactory<String, LocalTime> {

        private final ZoneId zoneId;

        public String2LocalTimeConverterFactory() {
            this(ZoneId.systemDefault());
        }

        public String2LocalTimeConverterFactory(ZoneId zoneId) {
            this.zoneId = zoneId;
        }

        @Override
        public <T extends LocalTime> Converter<String, T> getConverter(Class<T> targetType) {
            return source -> {
                Date date;
                try {
                    date = (Date) (DateUtils.parseDate(source, Locale.ENGLISH,
                            SUPPORTED_DATE_TIME_PATTERNS));
                } catch (ParseException e) {
                    throw new DateConversionException(e.getMessage(), e);
                }
                return targetType.cast(date.toInstant().atZone(zoneId).toLocalTime());
            };
        }
    }

    public static class LocalDate2StringConverterFactory
            implements ConverterFactory<LocalDate, String> {

        private final DateTimeFormatter dateTimeFormatter;

        public LocalDate2StringConverterFactory() {
            this(DateTimeFormatter.ISO_LOCAL_DATE);
        }

        public LocalDate2StringConverterFactory(DateTimeFormatter dateTimeFormatter) {
            this.dateTimeFormatter = dateTimeFormatter;
        }

        @Override
        public <T extends String> Converter<LocalDate, T> getConverter(Class<T> targetType) {
            return source -> {
                return targetType.cast(source.format(dateTimeFormatter));
            };
        }
    }

    public static class LocalDateTime2StringConverterFactory
            implements ConverterFactory<LocalDateTime, String> {

        private final DateTimeFormatter dateTimeFormatter;

        public LocalDateTime2StringConverterFactory() {
            this(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }

        public LocalDateTime2StringConverterFactory(DateTimeFormatter dateTimeFormatter) {
            this.dateTimeFormatter = dateTimeFormatter;
        }

        @Override
        public <T extends String> Converter<LocalDateTime, T> getConverter(Class<T> targetType) {
            return source -> {
                return targetType.cast(source.format(dateTimeFormatter));
            };
        }
    }

    public static class LocalDateTime2LongConverterFactory
            implements ConverterFactory<LocalDateTime, Long> {

        private final ZoneId zoneId;

        public LocalDateTime2LongConverterFactory() {
            this(ZoneId.systemDefault());
        }

        public LocalDateTime2LongConverterFactory(ZoneId zoneId) {
            this.zoneId = zoneId;
        }

        @Override
        public <T extends Long> Converter<LocalDateTime, T> getConverter(Class<T> targetType) {
            return source -> {
                return targetType.cast(source.atZone(zoneId).toInstant().toEpochMilli());
            };
        }
    }

    public static class DateConversionException extends ConversionException {

        private static final long serialVersionUID = 1566052960250773052L;


        public DateConversionException(String message, Throwable cause) {
            super(message, cause);
        }

    }

}

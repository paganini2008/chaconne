package com.github.chaconne.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

/**
 * 
 * @Description: ExceptionUtils
 * @Author: Fred Feng
 * @Date: 14/04/2025
 * @Version 1.0.0
 */
public abstract class ExceptionUtils {

    private static final String NEWLINE = System.getProperty("line.separator");

    public static String[] toArray(Throwable e) {
        if (e == null) {
            return new String[0];
        }
        String[] array = null;
        PrintWriter writer = null;
        try {
            StringArrayWriter out = new StringArrayWriter();
            writer = new PrintWriter(out);
            e.printStackTrace(writer);
            array = out.toArray();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        List<String> results = new ArrayList<String>();
        for (String line : array) {
            if (line.equals(NEWLINE)) {
                continue;
            }
            if (line.startsWith("\t")) {
                line = line.replace("\t", "    ");
            }
            results.add(line);
        }
        return results.toArray(new String[0]);
    }

    public static String toString(Throwable e) {
        PrintWriter writer = null;
        try {
            StringWriter out = new StringWriter();
            writer = new PrintWriter(out);
            e.printStackTrace(writer);
            return out.toString();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public static boolean ignoreException(Throwable e, Class<?>[] exceptionClasses) {
        if (ArrayUtils.isNotEmpty(exceptionClasses)) {
            for (Class<?> exceptionClass : exceptionClasses) {
                if (exceptionClass.isAssignableFrom(e.getClass())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Throwable getOriginalException(Exception e) {
        if (e instanceof InvocationTargetException) {
            return ((InvocationTargetException) e).getTargetException();
        }
        return e;
    }

    static class StringArrayWriter extends Writer {

        private final List<String> lines = new ArrayList<String>();

        @Override
        public void write(String str) {
            lines.add(str);
        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            lines.add(new String(cbuf, off, len));
        }

        @Override
        public void flush() throws IOException {}

        @Override
        public void close() throws IOException {
            lines.clear();
        }

        public String[] toArray() {
            return lines.toArray(new String[0]);
        }
    }

}

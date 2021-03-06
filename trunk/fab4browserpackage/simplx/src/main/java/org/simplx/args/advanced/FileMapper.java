/*
 * Copyright (c) 2009, 2010, Ken Arnold All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the myself nor the names of its contributors may be used
 * to endorse or promote products derived from this software without specific
 * prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * @SimplxCopyright
 */

package org.simplx.args.advanced;

import org.simplx.args.CommandLine;
import org.simplx.args.CommandLineException;
import org.simplx.args.CommandOpt;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.regex.Pattern;

/**
 * This class implements the standard mapping for {@link File} options. If you
 * are writing a custom mapper, you may find it useful to extend this; see
 * {@link MapperAdaptor} for an overview of why you would or would not write
 * custom mappers.
 */
public class FileMapper implements Mapper {
    private static final ObjectMapper objMapper = new ObjectMapper();

    /** Creates a new file mapper. */
    public FileMapper() {
    }

    private static final Pattern HAS_MKDIR = Pattern.compile(
            "(,|^)\\s*!\\sm*kdirs?\\s*([,|$])");

    /**
     * Returns the result of invoking {@link ObjectMapper#map} with the
     * parameter values.
     */
    @Override
    public Object map(CharSequence valString, Field field, Class type,
            CommandOpt anno, CommandLine line) {

        return objMapper.map(valString, field, type, anno, line);
    }

    /**
     * Handles the <tt>mkdir</tt>/<tt>mkdirs</tt> command check.  If the
     * directory cannot be created, it passes through the {@link IOException}
     * wrapped in a {@link CommandLineException}
     *
     * @return <tt>true</tt>
     *
     * @see File#mkdirs()
     */
    @Override
    public Boolean check(String check, Object val, Field field, Class type,
            CommandOpt anno, CommandLine line) throws IOException {

        if (check.equals("mkdir") || check.equals("mkdirs")) {
            mkdirs(((File) val).getPath());
            return true;
        }
        return null;
    }

    /**
     * Creates the given directory if needed, including any intermediate missing
     * directories.
     *
     * @param path The path for the directory.
     *
     * @throws IOException The path already exists as a file or {@link
     *                     File#mkdirs} returns {@code false}.
     */
    private static void mkdirs(String path) throws IOException {
        File dir = new File(path);
        if (dir.isDirectory()) {
            return;
        }

        if (dir.exists() && !dir.isDirectory()) {
            throw new IOException(
                    "mkdirs: " + dir + " exists but is not a directory");
        }

        if (!dir.mkdirs()) {
            throw new IOException(
                    "mkdirs: " + dir + " Cannot create directory");
        }
    }

    /**
     * Returns <tt>"file"</tt> unless the check includes "mkdirs", for which it
     * returns <tt>"dir"</tt>.
     */
    @Override
    public CharSequence defaultValueName(Field field, Class type,
            CommandOpt anno, CommandLine line) {

        if (anno != null && HAS_MKDIR.matcher(anno.checks()).matches())
            return "dir";
        else
            return "path";
    }
}

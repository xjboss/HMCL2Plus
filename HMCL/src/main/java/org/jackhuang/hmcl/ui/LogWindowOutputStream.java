/*
 * Hello Minecraft!.
 * Copyright (C) 2013  huangyuhui <huanghongxun2008@126.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see {http://www.gnu.org/licenses/}.
 */
package org.jackhuang.hmcl.ui;

import java.io.OutputStream;
import java.util.Objects;
import javax.swing.SwingUtilities;
import org.jackhuang.hmcl.util.log.Level;

/**
 *
 * @author huangyuhui
 */
public class LogWindowOutputStream extends OutputStream {

    private final LogWindow txt;
    private final Level sas;

    public LogWindowOutputStream(LogWindow logWindow, Level l) {
        txt = Objects.requireNonNull(logWindow);
        sas = Objects.requireNonNull(l);
    }

    @Override
    public final void write(byte[] arr) {
        write(arr, 0, arr.length);
    }

    @Override
    public final void write(byte[] arr, int off, int len) {
        append(new String(arr, off, len));
    }
    
    final Object obj = new Object();
    Level lastLevel = null;

    private void append(final String str) {
        synchronized(this) {
            if (manual) return;
        }
        SwingUtilities.invokeLater(() -> {
            Level level = Level.guessLevel(str);
            if (level == null) level = lastLevel;
            else lastLevel = level;
            txt.log(str, Level.mergeLevel(sas, level));
        });
    }

    @Override
    public final void write(int i) {
        append(new String(new byte[] { (byte) i }));
    }
    
    boolean manual = false;
    
    public void log(String s, Level l) {
        synchronized(this) {
            manual = true;
            System.out.print(s);
            manual = false;
        }
        if (l == null) append(s);
        else
        SwingUtilities.invokeLater(() -> {
            txt.log(s, l);
        });
    }
}
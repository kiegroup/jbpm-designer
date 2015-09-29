package org.jbpm.designer.web.server;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

/**
 * @author Tim Urmancheev
 */
public final class FileUtil {
  private static final long MAX_WAIT_TIME = 10000;
  private static final long SLEEP_TIME = 9;


  public static void lockedWrite(String content, File file) throws IOException {
    RandomAccessFile randomAccessFile = null;
    try {
      randomAccessFile = new RandomAccessFile(file, "rw");
      FileChannel channel = randomAccessFile.getChannel();
      FileLock lock = tryLock(channel);

      PrintWriter writer = null;
      try {
        randomAccessFile.setLength(0);
        OutputStream out = Channels.newOutputStream(channel);

        writer = new PrintWriter(new IgnoreCloseOutputStream(out));
        writer.write(content);
        writer.flush();
      }
      finally {
        if (writer != null) {
          writer.close();
        }
        lock.release();
      }
    }
    finally {
      if (randomAccessFile != null) {
        randomAccessFile.close();
      }
    }
  }

  private static FileLock tryLock(FileChannel channel) throws IOException {
    FileLock lock = safeTryLock(channel);
    long timeLeft = MAX_WAIT_TIME;
    while (lock == null) {
      try {
        if (timeLeft < 0)
          throw new IOException("Could not lock file, wait time " + MAX_WAIT_TIME + " passed");
        timeLeft -= SLEEP_TIME;
        Thread.sleep(SLEEP_TIME);
        lock = safeTryLock(channel);
      }
      catch (InterruptedException ignored) {
      }
    }
    return lock;
  }

  private static FileLock safeTryLock(FileChannel channel) throws IOException {
    try {
      return channel.lock();
    }
    catch (OverlappingFileLockException ex) {
      return null;
    }
  }

  static private class IgnoreCloseOutputStream extends BufferedOutputStream {

    /**
     * Creates an output stream filter built on top of the specified
     * underlying output stream.
     *
     * @param out the underlying output stream to be assigned to
     *            the field <tt>this.out</tt> for later use, or
     *            <code>null</code> if this instance is to be
     *            created without an underlying stream.
     */
    public IgnoreCloseOutputStream(OutputStream out) {
      super(out);
    }

    @Override
    public void close() throws IOException {
    }
  }
}

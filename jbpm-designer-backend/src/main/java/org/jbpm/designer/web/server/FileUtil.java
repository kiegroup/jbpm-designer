package org.jbpm.designer.web.server;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

import org.apache.commons.io.FileUtils;

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

      try {
        randomAccessFile.setLength(0);
        OutputStream out = Channels.newOutputStream(channel);

        FileUtils.writeStringToFile(file, content);

        out.flush();
      }
      finally {
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
}

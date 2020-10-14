import java.io.*;  // added

public class  SimpleLineReader {
    private FilterInputStream  s;

    public  SimpleLineReader(InputStream  anIS) {
        s = new DataInputStream(anIS);
    }

       // other read() methods using stream s

    public String  readLine() throws IOException {
        char[]  buffer = new char[100];
        int     offset = 0;
        byte    thisByte;

        try {
loop:        while (offset < buffer.length) {
                switch (thisByte = (byte) s.read()) {
                    case '\n':
                        break loop;
                    case '\r':
                        byte  nextByte = (byte) s.read();

                        if (nextByte != '\n') {
                            if (!(s instanceof PushbackInputStream)) {
                                s = new PushbackInputStream(s);
                            }
                            ((PushbackInputStream) s).unread(nextByte);
                        }
                        break loop;
                    default:
                        buffer[offset++] = (char) thisByte;
                        break;
                }
            }
        } catch (EOFException e) {
            if (offset == 0)
                return null;
        }
        return String.copyValueOf(buffer, 0, offset);
    }
}

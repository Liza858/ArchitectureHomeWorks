package bash;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;


public class TestBash {

    private CommandLine cl;

    @Before
    public void setUp() {
        cl = new CommandLine();
    }

    @Test
    public void echoTest() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("echo 123\n");
        sb.append("echo hello world\n");
        sb.append("  echo   hello   world  \n");
        sb.append("exit\n");
        InputStream in = new ByteArrayInputStream(sb.toString().getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        System.setIn(in);

        cl.run();
        String result = out.toString();

        sb = new StringBuilder();
        sb.append("> 123\n");
        sb.append("> hello world\n");
        sb.append("> hello world\n");
        sb.append("> exit\n");

        Assert.assertEquals(sb.toString(), result);
    }

    @Test
    public void variablesTest() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("var=hello\n");
        sb.append("echo $var\n");
        sb.append("var=world\n");
        sb.append("   echo   $var  \n");
        sb.append("file=file1.txt\n");
        sb.append("  cat    $file  \n");
        sb.append("exit\n");
        InputStream in = new ByteArrayInputStream(sb.toString().getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        System.setIn(in);

        cl.run();
        String result = out.toString();

        sb = new StringBuilder();
        sb.append("> add variable successful\n");
        sb.append("> hello\n");
        sb.append("> add variable successful\n");
        sb.append("> world\n");
        sb.append("> add variable successful\n");
        sb.append("> aaa\n\n");
        sb.append("> exit\n");

        Assert.assertEquals(sb.toString(), result);
    }

    @Test
    public void quotingTest() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("echo \"123\"\n");
        sb.append("echo '\"123\"'\n");
        sb.append("echo '*hello\"\"'\n");
        sb.append("   cat   \"file1.txt\"  \n");
        sb.append("exit\n");
        InputStream in = new ByteArrayInputStream(sb.toString().getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        System.setIn(in);

        cl.run();
        String result = out.toString();

        sb = new StringBuilder();
        sb.append("> 123\n");
        sb.append("> \"123\"\n");
        sb.append("> *hello\"\"\n");
        sb.append("> aaa\n\n");
        sb.append("> exit\n");

        Assert.assertEquals(sb.toString(), result);
    }

    @Test
    public void catTest() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("cat  file1.txt\n");
        sb.append("  cat   file2.txt  \n");
        sb.append("exit\n");
        InputStream in = new ByteArrayInputStream(sb.toString().getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        System.setIn(in);

        cl.run();
        String result = out.toString();

        sb = new StringBuilder();
        sb.append("> aaa\n\n");
        sb.append("> one\ntwo\nthree\n\n");
        sb.append("> exit\n");

        Assert.assertEquals(sb.toString(), result);
    }

    @Test
    public void wcTest() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("wc  file1.txt\n");
        sb.append("  wc   file2.txt  \n");
        sb.append(" wc   file3.txt\n");
        sb.append("exit\n");
        InputStream in = new ByteArrayInputStream(sb.toString().getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        System.setIn(in);

        cl.run();
        String result = out.toString();

        sb = new StringBuilder();
        sb.append("> 1 1 4\n");
        sb.append("> 3 3 14\n");
        sb.append("> 2 3 14\n");
        sb.append("> exit\n");

        Assert.assertEquals(sb.toString(), result);
    }

    @Test
    public void pwdTest() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("pwd\n");
        sb.append("exit\n");
        InputStream in = new ByteArrayInputStream(sb.toString().getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        System.setIn(in);

        cl.run();
        String result = out.toString();

        String path = new File("").getAbsolutePath();

        sb = new StringBuilder();
        sb.append("> " + path + "\n");
        sb.append("> exit\n");

        Assert.assertEquals(sb.toString(), result);
    }


    @Test
    public void pipesTest() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("cat file2.txt | wc\n");
        sb.append(" echo 123 | wc\n");
        sb.append("cat file3.txt | grep world\n");
        sb.append("exit\n");
        InputStream in = new ByteArrayInputStream(sb.toString().getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        System.setIn(in);

        cl.run();
        String result = out.toString();

        sb = new StringBuilder();
        sb.append("> 3 3 14\n");
        sb.append("> 1 1 3\n");
        sb.append("> Hello world\n\n");
        sb.append("> exit\n");

        Assert.assertEquals(sb.toString(), result);
    }

    @Test
    public void grepTest() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("grep a file1.txt\n");
        sb.append("grep tw file2.txt\n");
        sb.append("grep Hello file3.txt\n");
        sb.append("grep -i hello file3.txt\n");
        sb.append("grep -w on file2.txt\n");
        sb.append("grep -w one file2.txt\n");
        sb.append("grep -A 1 one file2.txt\n");
        sb.append("grep -A 2 one file2.txt\n");
        sb.append("exit\n");

        InputStream in = new ByteArrayInputStream(sb.toString().getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        System.setIn(in);

        cl.run();
        String result = out.toString();

        sb = new StringBuilder();
        sb.append("> aaa\n\n");
        sb.append("> two\n\n");
        sb.append("> Hello world\n\n");
        sb.append("> Hello world\n\n");
        sb.append("> ");
        sb.append("> one\n\n");
        sb.append("> one\ntwo\n\n");
        sb.append("> one\ntwo\nthree\n\n");
        sb.append("> exit\n");

        Assert.assertEquals(sb.toString(), result);
    }

}

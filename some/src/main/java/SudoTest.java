import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class SudoTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        //String[] cmds = {shellName, shellParam, "echo \"" + sudoPasswd + "\" | " + sudoCmd + " -S " + cmd};
        String cmd = "echo 'KSM4eas$2017'  | sudo -S  chmod 777 -R /home/kduser/cloudbackup";
        String[] cmdString = {"/bin/bash", "-c", cmd};
        Process process = Runtime.getRuntime().exec(cmdString);
        new Thread(()->{
            InputStream inputStream = process.getErrorStream();
            StringWriter sw = new StringWriter();
            try{
                int c;
                while((c = inputStream.read()) != -1){
                    sw.write(c);
                }
            }catch(IOException e){
                sw.write("check Failed");
            }
            System.out.println(sw.toString());
        }).start();
        process.waitFor();
        Thread.sleep(10000);
        System.out.println("end");

    }

}

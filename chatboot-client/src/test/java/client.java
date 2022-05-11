import com.lesterlaucn.chatboot.client.ClientSender.ChatSender;
import com.lesterlaucn.chatboot.client.client.ClientSession;
import com.lesterlaucn.chatboot.protoc.UserDTO;
import org.junit.Test;

/**
 * Created by lesterlaucn
 */

public class client
{

    @Test
    public void sendChatMsg()
    {
        ChatSender sender = new ChatSender();
        UserDTO user = new UserDTO();
        user.setUserId("1");
        user.setNickName("张三");
        user.setSessionId("-1");
        sender.setSession(new ClientSession(null));
        sender.setUser(user);
        sender.sendChatMsg("dd", "1");

        try
        {
            Thread.sleep(1000000);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}

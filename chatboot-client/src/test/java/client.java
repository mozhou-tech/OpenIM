import com.lesterlaucn.chatboot.im.common.bean.UserDTO;
import com.lesterlaucn.chatboot.imClient.ClientSender.ChatSender;
import com.lesterlaucn.chatboot.imClient.client.ClientSession;
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

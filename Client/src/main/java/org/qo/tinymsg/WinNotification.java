package org.qo.tinymsg;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;

public class WinNotification {
    public static void main(String[] args) {
        String title = "通知标题";
        String content = "这是一个测试通知";

        // 创建通知对象
        WinToastNotification notification = new WinToastNotification(title, content);

        // 发送通知
        //HRESULT result = WinToast.sendNotification(notification);

        //if (WinNT.S_OK.equals(result)) {
        //    System.out.println("通知发送成功！");
        //} else {
        //    System.err.println("通知发送失败！");
        //}
    }
}

class WinToastNotification {
    private static final String CLSID_ShellLink = "72C24DD5-D70A-438B-8A42-98424B88AFB8";

    private final String title;
    private final String content;

    public WinToastNotification(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}

package ru.linachan;

import ru.linachan.yggdrasil.YggdrasilAgent;
import ru.linachan.yggdrasil.YggdrasilException;

public class YggdrasilAgentLauncher {
    
    public static void main(String[] args) {
        YggdrasilAgent agent = new YggdrasilAgent("conf/YggdrasilAgent.ini");
        try {
            agent.start();
        } catch (YggdrasilException e) {
            System.err.println(e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
}

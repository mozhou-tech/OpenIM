package com.lesterlaucn.chatboot.client.clientCommand;

import java.util.Scanner;

public interface BaseCommand
{
    void exec(Scanner scanner);

    String getKey();

    String getTip();
}

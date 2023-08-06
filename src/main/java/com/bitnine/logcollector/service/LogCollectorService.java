package com.bitnine.logcollector.service;

import org.apache.commons.io.input.ReversedLinesFileReader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.List;

@Service
public class LogCollectorService {

    private final String targetDirectory = "C:\\work\\AgensGraph-2.12.0\\data\\agdata\\log\\";

    private WatchKey watchKey;

    public void serviceStart(){

        try{
            WatchService watchService = FileSystems.getDefault().newWatchService();

            Path path = Paths.get(targetDirectory);
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE
                    , StandardWatchEventKinds.ENTRY_DELETE
                    , StandardWatchEventKinds.ENTRY_MODIFY
                    , StandardWatchEventKinds.OVERFLOW);

            Thread thread = new Thread(() -> {
               while (true){
                   try {
                       watchKey = watchService.take();
                   } catch (InterruptedException e) {
                       throw new RuntimeException(e);
                   }

                   List<WatchEvent<?>> events = watchKey.pollEvents();

                   for (WatchEvent<?> event : events) {
                       WatchEvent.Kind<?> kind = event.kind();

                       Path paths = (Path) event.context();

                        if(kind.equals(StandardWatchEventKinds.ENTRY_CREATE)){
                            System.err.println("파일이 새로 생성됨");
                            System.err.println("이벤트가 일어난 파일이름");
                            System.err.println(paths.getFileName());
                        } else if (kind.equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                            
                        } else if (kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {

                            System.err.println(paths.getFileName());
                            String logContent =  readFile(targetDirectory + paths.getFileName());
                            System.err.println("파일이 수정됨");
                            System.err.println("수정된 파일 내용");
                            System.err.println(logContent);
                        } else if (kind.equals(StandardWatchEventKinds.OVERFLOW)) {

                        } else {

                        }
                   }

                   if(!watchKey.reset()) {
                       try {
                           watchService.close();
                       } catch (IOException e) {
                           System.err.println("watchKey 갱신중 에러발생");
                           //throw new RuntimeException(e);
                       }
                   }
               }
            });

            thread.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public String readFile(String filePath){
        //System.err.println("LogCollectorService readFile filePath");
        //System.err.println(filePath);
        File file = new File(filePath);
        long fileLength = file.length();
        //System.err.println("LogCollectorService readFile fileLength");
        //System.err.println(fileLength);
        ReversedLinesFileReader reversedLinesFileReader = null;
        //RandomAccessFile randomAccessFile = null;
        String logContent = null;

        try {
            reversedLinesFileReader = new ReversedLinesFileReader(file, Charset.forName("UTF-8"));
            logContent = reversedLinesFileReader.readLine();


        }catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return logContent;
    }
}

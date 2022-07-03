package com.ouyangliuy.jdk9;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class Test {
    public static void main(String[] args) {
        System.out.println(new String("xxxxx"));


        System.out.println("wwwwwww".strip());
        System.out.println("wwwwwww".stripTrailing());
        System.out.println();
        Stream<String> lines = "wwwwwww".lines();
        lines.count();


        Optional<String> optional = Optional.empty();
        //JDK8 判断value是否存在
        System.out.println(optional.isPresent());
        //JDK11 判断value是否为空
        System.out.println(optional.isEmpty());

        //JDK10 返回value,如果为null则直接抛出 NoSuchElementExpception
        Optional<String> optional2 = Optional.of("element1");
        String value = optional2.orElseThrow();
        System.out.println(value);

        //JDK9  value非空,执行参数1功能,如果value为空,执行参数2功能
        Optional<String> optional3 = Optional.empty();// Optional.of("element1");
        optional.ifPresentOrElse((v) -> System.out.println("value为" + v), () -> System.out.println("value为null"));

        // JDK9 value非空,返回对应的Optional,value为空,返回形参封装的Optional
        Optional<String> optional4 = Optional.empty();// Optional.of("element1");
        Optional<String> optional5 = optional4.or(() -> Optional.of("element2"));
        System.out.println(optional5);

        // JDK9 value非空,返回一个仅包含此value的Steam,否则,返回一个空的Stream
        Optional<String> optional6 = Optional.of("element3");//Optional.empty();
        Stream<String> stream = optional6.stream();
        stream.forEach(System.out::println);


        String code = "public void print(Object o) {" +
                """
                    System.out.println(Objects.toString(o));
                }
                """;
        System.out.println(code);


    }

    private void httpClientSync() throws IOException, InterruptedException {
        //HttpClient 替换原有的HttpUrlConnection  同步方式
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:8080/demo")).build();
        HttpResponse.BodyHandler<String> responseBodyHandler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, responseBodyHandler);
        String body = response.body();
        System.out.println(body);
    }

    private void httpClientAsync() {
        //HttpClient 替换原有的HttpUrlConnection  异步方式
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:8080/demo")).build();
        HttpResponse.BodyHandler<String> responseBodyHandler = HttpResponse.BodyHandlers.ofString();
        CompletableFuture<HttpResponse<String>> sendAsync = client.sendAsync(request, responseBodyHandler);
        sendAsync.thenApply(HttpResponse::body).thenAccept(System.out::println);
    String aa = """
            select *
            from aa
            where aa >0
            """;
        String story = """
"When I use a word," Humpty Dumpty said,
in rather a scornful tone, "it means just what I
choose it to mean - neither more nor less."
"The question is," said Alice, "whether you
can make words mean so many different things."
"The question is," said Humpty Dumpty,
"which is to be master - that's all."
""";
        String code =
                """
                String text = \"""
                A text block inside a text block
                \""";
                """;
    }

}

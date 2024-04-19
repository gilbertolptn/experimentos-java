package dev.gibatech.exp.jdbi.infra.postgresl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jdbi.v3.core.HandleConsumer;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Repository
public class MessagemRepositoryTests {

    public static final String JDBC_URL = "jdbc:postgresql://localhost:5432/postgres?user=postgres&password=postgres";
    StopWatch watch = new StopWatch();

    public MessagemRepositoryTests() throws InterruptedException, ExecutionException {

        HikariConfig hc = new HikariConfig();
        hc.setJdbcUrl(JDBC_URL);
        hc.setMaximumPoolSize(5);
        Jdbi jdbi = Jdbi.create(new HikariDataSource(hc));

        AtomicInteger inc = new AtomicInteger();
        watch.start();

        ExecutorService e = Executors.newVirtualThreadPerTaskExecutor();
        e.submit(() ->
            IntStream.range(0, 1_000)
                    .parallel()
                    .<HandleConsumer<RuntimeException>>
                            mapToObj(i -> handle ->
                            handle.execute("insert into mensagens (mensagem, data_hora) values (?, ?)",
                    "teste-" + inc.getAndIncrement(), LocalDateTime.now())).forEach(jdbi::useHandle)
        ).get();
        e.shutdown();

        watch.stop();
        System.out.println(watch.prettyPrint());
        watch.start();

        jdbi.withHandle(handle -> {
                handle.registerRowMapper(ConstructorMapper.factory(Mensagem.class));
                return handle.createQuery("select id, mensagem, data_hora from mensagens LIMIT 2")
                        .mapTo(Mensagem.class).list();
                }).stream()
                .forEach(m -> System.out.println(m));

        watch.stop();
        System.out.println(watch.shortSummary());
    }
}

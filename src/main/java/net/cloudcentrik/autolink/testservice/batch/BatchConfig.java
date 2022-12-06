package net.cloudcentrik.autolink.testservice.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@Component
public class BatchConfig {

    @Value("${file.input}")
    private String fileInput;

    /*@Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobRepository jobRepository;*/

    @Bean
    public FlatFileItemReader<Coffee> reader() {
        return new FlatFileItemReaderBuilder<Coffee>().name("coffeeItemReader")
                .resource(new ClassPathResource(fileInput))
                .delimited()
                .names(new String[] { "brand", "origin", "characteristics" })
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Coffee>() {{
                    setTargetType(Coffee.class);
                }})
                .build();
    }

    @Bean
    public CoffeeItemProcessor processor() {
        return new CoffeeItemProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Coffee> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Coffee>().itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO coffee (brand, origin, characteristics) VALUES (:brand, :origin, :characteristics)")
                .dataSource(dataSource)
                .build();
    }

    /*@Bean
    public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }*/

    @Bean
    public Job importUserJob(JobRepository jobRepository, JobCompletionNotificationListener listener, Step step1) {
        return new JobBuilder("footballJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager, JdbcBatchItemWriter<Coffee> writer) {
        return new StepBuilder("sampleStep", jobRepository)
                .<Coffee, Coffee> chunk(10,transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer)
                .build();
    }

    /*@Bean
    public Step myStep(JobRepository jobRepository, Tasklet myTasklet, PlatformTransactionManager transactionManager) {
        return new StepBuilder("myStep", jobRepository)
                .tasklet(myTasklet, transactionManager)
                .build();
    }

    @Bean
    public Job myJob(JobRepository jobRepository, Step step) {
        return new JobBuilder("myJob", jobRepository)
                .start(step)
                .build();
    }

    @Bean
    public JsonItemReader<WikiPageViews> jsonItemReader() {
        return new JsonItemReaderBuilder<WikiPageViews>()
                .jsonObjectReader(new JacksonJsonObjectReader<>(WikiPageViews.class))
                .resource(new ClassPathResource("trades.json"))
                .name("tradeJsonItemReader")
                .build();
    }

    @Bean
    public JsonFileItemWriter<WikiPageViews> jsonFileItemWriter() {
        return new JsonFileItemWriterBuilder<WikiPageViews>()
                .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
                .resource((WritableResource) new ClassPathResource("trades.json"))
                .name("tradeJsonFileItemWriter")
                .build();
    }*/

}

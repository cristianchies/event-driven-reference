package org.github.rodrigorfk.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.github.rodrigorfk.customer.data.CustomerEntity;
import org.github.rodrigorfk.customer.messaging.CustomerEventQueue;
import org.github.rodrigorfk.customer.repostiory.CustomerRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.concurrent.BlockingQueue;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * @author Rodrigo Fior Kuntzer <rodrigo.kuntzer@cwi.com.br>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerApplicationTest {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private MessageCollector collector;
    @Autowired
    private CustomerEventQueue queue;
    private MockMvc mockMvc;
    @Autowired
    protected WebApplicationContext wac;
    @Autowired
    private ObjectMapper mapper;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(this.wac).build();
    }

    @Test
    public void initContext(){

    }

    @Test
    public void saveCustomer() throws Exception {
        BlockingQueue<Message<?>> channel = collector.forChannel(queue.output());
        channel.clear();

        CustomerEntity entity = CustomerEntity.builder()
                .id("teste1").name("Rodrigo").build();

        this.mockMvc.perform(post("/customerEntities")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsBytes(entity))
        )
                .andExpect(status().isCreated());


        Assert.assertNotNull(customerRepository.findOne("teste1"));
        Assert.assertEquals(1, channel.size());
        Assert.assertEquals("created", channel.take().getHeaders().get("action"));
    }
}
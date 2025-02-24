package org.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
abstract class AbstractIntegration {
    @Autowired
    protected MockMvc mockmvc;
    @Autowired
    protected ObjectMapper mapper;
}
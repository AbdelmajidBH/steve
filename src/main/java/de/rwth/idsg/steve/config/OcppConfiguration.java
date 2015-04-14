package de.rwth.idsg.steve.config;

import de.rwth.idsg.steve.MediatorInInterceptor;
import de.rwth.idsg.steve.MessageIdInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.xml.ws.soap.SOAPBinding;

/**
 * Configuration and beans related to OCPP clients/services.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 18.11.2014
 */
@Configuration
public class OcppConfiguration {

    @Autowired private ocpp.cs._2010._08.CentralSystemService ocpp12Server;
    @Autowired private ocpp.cs._2012._06.CentralSystemService ocpp15Server;

    @PostConstruct
    public void init() {
        MessageIdInterceptor midi = new MessageIdInterceptor();

        createRouterService();
        createOcpp12Service(midi);
        createOcpp15Service(midi);
    }

    @Bean
    @Qualifier("ocpp12")
    public JaxWsProxyFactoryBean ocpp12ClientFactory() {
        JaxWsProxyFactoryBean f = new JaxWsProxyFactoryBean();
        f.setBindingId(SOAPBinding.SOAP12HTTP_BINDING);
        f.getFeatures().add(new WSAddressingFeature());
        f.setServiceClass(ocpp.cp._2010._08.ChargePointService.class);
        return f;
    }

    @Bean
    @Qualifier("ocpp15")
    public JaxWsProxyFactoryBean ocpp15ClientFactory() {
        JaxWsProxyFactoryBean f = new JaxWsProxyFactoryBean();
        f.setBindingId(SOAPBinding.SOAP12HTTP_BINDING);
        f.getFeatures().add(new WSAddressingFeature());
        f.setServiceClass(ocpp.cp._2012._06.ChargePointService.class);
        return f;
    }

    /**
     * Just a dummy service to route incoming messages to the appropriate service version.
     */
    private void createRouterService() {
        JaxWsServerFactoryBean f = new JaxWsServerFactoryBean();
        f.setServiceBean(ocpp12Server);
        f.setAddress("/CentralSystemService");
        f.getInInterceptors().add(new MediatorInInterceptor());
        f.create();
    }

    private void createOcpp12Service(MessageIdInterceptor midi) {
        JaxWsServerFactoryBean f = new JaxWsServerFactoryBean();
        f.setServiceBean(ocpp12Server);
        f.setAddress("/CentralSystemServiceOCPP12");
        f.getInInterceptors().add(midi);
        f.create();
    }

    private void createOcpp15Service(MessageIdInterceptor midi) {
        JaxWsServerFactoryBean f = new JaxWsServerFactoryBean();
        f.setServiceBean(ocpp15Server);
        f.setAddress("/CentralSystemServiceOCPP15");
        f.getInInterceptors().add(midi);
        f.create();
    }
}
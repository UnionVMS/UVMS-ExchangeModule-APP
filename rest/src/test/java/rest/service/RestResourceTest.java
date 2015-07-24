package rest.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import eu.europa.ec.fisheries.uvms.exchange.rest.service.ExchangeRestResource;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.ResponseCode;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.ResponseDto;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeService;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeServiceException;
import eu.europa.ec.fisheries.uvms.exchange.service.mockdata.MockData;
import eu.europa.ec.fisheries.wsdl.types.ModuleObject;

public class RestResourceTest {

    private static final Long ID = 1L;
    private static final Integer EXCHANGE_LIST_SIZE = 3;

    List<ModuleObject> DTO_LIST = MockData.getDtoList(EXCHANGE_LIST_SIZE);
    ModuleObject DTO = MockData.getDto(ID);

    private final ResponseDto ERROR_RESULT;
    private final ResponseDto SUCCESS_RESULT;
    private final ResponseDto SUCCESS_RESULT_LIST;
    private final ResponseDto SUCCESS_RESULT_DTO;

    ExchangeRestResource SERVICE_NULL = new ExchangeRestResource();

    @Mock
    ExchangeService serviceLayer;

    @InjectMocks
    ExchangeRestResource exchangeRestResource;

    public RestResourceTest() {
        ERROR_RESULT = new ResponseDto(ResponseCode.ERROR);
        SUCCESS_RESULT = new ResponseDto(ResponseCode.OK);
        SUCCESS_RESULT_LIST = new ResponseDto(DTO_LIST, ResponseCode.OK);
        SUCCESS_RESULT_DTO = new ResponseDto(DTO, ResponseCode.OK);
    }

    @BeforeClass
    public static void setUpClass() {

    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test get list with a happy outcome
     *
     * @throws ExchangeServiceException
     */
    @Test
    public void testGetVesselList() throws ExchangeServiceException {
        doReturn(DTO_LIST).when(serviceLayer).getList();
        ResponseDto result = exchangeRestResource.getList();
        assertEquals(SUCCESS_RESULT_LIST.toString(), result.toString());
    }

    /**
     * Test get list when the injected EJB is null
     *
     * @throws ExchangeServiceException
     */
    @Test
    public void testGetVesselListNull() throws ExchangeServiceException {
        ResponseDto result = SERVICE_NULL.getList();
        assertEquals(ERROR_RESULT.toString(), result.toString());
    }

    /**
     * Test get by id with a happy outcome
     *
     * @throws ExchangeServiceException
     */
    @Test
    public void testGetVesselById() throws ExchangeServiceException {
        doReturn(DTO).when(serviceLayer).getById(ID);
        ResponseDto result = exchangeRestResource.getById(ID);
        Mockito.verify(serviceLayer).getById(ID);
        assertEquals(SUCCESS_RESULT_DTO.toString(), result.toString());

    }

    /**
     * Test get by id when the injected EJB is null
     *
     * @throws ExchangeServiceException
     */
    @Test
    public void testGetVesselByIdNull() throws ExchangeServiceException {
        ResponseDto result = SERVICE_NULL.getById(ID);
        assertEquals(ERROR_RESULT.toString(), result.toString());
    }

    /**
     * Test create with a happy outcome
     *
     * @throws ExchangeServiceException
     */
    @Test
    public void testCreateVessel() throws ExchangeServiceException {
        ResponseDto result = exchangeRestResource.create(DTO);
        Mockito.verify(serviceLayer).create(DTO);
        assertEquals(SUCCESS_RESULT.toString(), result.toString());
    }

    /**
     * Test create when the injected EJB is null
     */
    @Test
    public void testCreateVesselNull() {
        ResponseDto result = SERVICE_NULL.create(DTO);
        assertEquals(ERROR_RESULT.toString(), result.toString());
    }

    /**
     * Test update with a happy outcome
     *
     * @throws ExchangeServiceException
     */
    @Test
    public void testUpdateVessel() throws ExchangeServiceException {
        ResponseDto result = exchangeRestResource.update(DTO);
        Mockito.verify(serviceLayer).update(DTO);
        assertEquals(SUCCESS_RESULT.toString(), result.toString());
    }

    /**
     * Test update when the injected EJB is null
     */
    @Test
    public void testUpdateVesselNull() {
        ResponseDto result = SERVICE_NULL.update(DTO);
        assertEquals(ERROR_RESULT.toString(), result.toString());
    }

}

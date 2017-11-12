package ru.akirakozov.sd.refactoring;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.akirakozov.sd.refactoring.dao.Product;
import ru.akirakozov.sd.refactoring.dao.ProductDao;
import ru.akirakozov.sd.refactoring.dao.ProductDaoImpl;

import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DriverManager.class, ProductDaoImpl.class})
public class DaoTest {
    private @Mock Statement mockStatement;
    private @Mock Connection mockConnection;
    private @Mock ResultSet mockMax;
    private @Mock ResultSet mockMin;
    private @Mock ResultSet mockSum;
    private @Mock ResultSet mockCount;

    private List<Product> list;

    @Before
    public void setup() throws SQLException {
        MockitoAnnotations.initMocks(DaoTest.class);
        PowerMock.mockStatic(DriverManager.class);

        EasyMock.expect(DriverManager.getConnection("jdbc:sqlite:test.db")).andStubReturn(mockConnection);
        PowerMock.replay(DriverManager.class);

        when(mockConnection.createStatement()).thenReturn(mockStatement);

        when(mockStatement.executeUpdate("")).thenReturn(1);
        when(mockStatement.executeUpdate(startsWith("CREATE TABLE IF NOT EXISTS PRODUCT"))).thenReturn(1);

        when(mockStatement.executeUpdate(startsWith("INSERT INTO PRODUCT")))
                .then(invocation -> {
                    String query = invocation.getArgument(0);
                    Pattern pattern = Pattern.compile("INSERT INTO PRODUCT \\(NAME, PRICE\\) VALUES \\(\"([^\"]+)\",(\\d+)\\)");
                    Matcher matcher = pattern.matcher(query);
                    assertTrue("Insert query '" + query + "' doesn't match pattern '" + matcher + "'", matcher.matches());
                    String name = matcher.group(1);
                    long price = Long.parseLong(matcher.group(2));
                    return list.add(Product.create(name, price)) ? 1 : 0;
                });

        when(mockStatement.executeQuery("SELECT * FROM PRODUCT ORDER BY PRICE DESC LIMIT 1"))
                .thenReturn(mockMax);
        when(mockMax.next()).thenReturn(true);
        when(mockMax.getString("name")).then(invocation -> getMaxProduct().getName());
        when(mockMax.getLong("price")).then(invocation -> getMaxProduct().getPrice());

        when(mockStatement.executeQuery("SELECT * FROM PRODUCT ORDER BY PRICE LIMIT 1"))
                .thenReturn(mockMin);
        when(mockMin.next()).thenReturn(true);
        when(mockMin.getString("name")).then(invocation -> getMinProduct().getName());
        when(mockMin.getLong("price")).then(invocation -> getMinProduct().getPrice());

        when(mockStatement.executeQuery("SELECT SUM(price) FROM PRODUCT"))
                .thenReturn(mockSum);
        when(mockSum.next()).thenReturn(true);
        when(mockSum.getLong(1)).then(invocation -> list.stream().mapToLong(Product::getPrice).sum());

        when(mockStatement.executeQuery("SELECT COUNT(*) FROM PRODUCT"))
                .thenReturn(mockCount);
        when(mockCount.next()).thenReturn(true);
        when(mockCount.getLong(1)).then(invocation -> list.size());
    }

    @Test
    public void testAll() throws SQLException {
        ProductDao dao = new ProductDaoImpl();
        verify(mockConnection).createStatement();
        verify(mockStatement).executeUpdate(startsWith("CREATE TABLE IF NOT EXISTS PRODUCT"));
        list = new ArrayList<>();

        Product iPhone5 = Product.create("iPhone 5", 400);
        Product iPhoneX = Product.create("iPhone X", 1150);
        Product pixel2 = Product.create("pixel 2", 1200);
        List<Product> products = Arrays.asList(
                Product.create("iPhone 8+", 850),
                Product.create("iPhone 5s", 450),
                iPhoneX,
                Product.create("iPhone 6+", 650),
                iPhone5,
                Product.create("iPhone 6", 600),
                Product.create("iPhone 8", 800)
        );

        products.stream().map(dao::addProduct).forEach(Assert::assertTrue);
        verify(mockStatement, times(products.size())).executeUpdate(startsWith("INSERT INTO PRODUCT"));

        assertEquals(Optional.of(iPhone5), dao.getMin());
        verifyParseProduct(mockMin, 1);

        assertEquals(Optional.of(iPhoneX), dao.getMax());
        verifyParseProduct(mockMax, 1);

        assertEquals(Optional.of((long) products.size()), dao.getCount());
        verify(mockCount).getLong(1);

        long sum = products.stream().mapToLong(Product::getPrice).sum();
        assertEquals(Optional.of(sum), dao.getSum());
        verify(mockSum).getLong(1);

        assertTrue(dao.addProduct(pixel2));
        verify(mockStatement, times(products.size() + 1)).executeUpdate(startsWith("INSERT INTO PRODUCT"));

        assertEquals(Optional.of(iPhone5), dao.getMin());
        verifyParseProduct(mockMin, 2);

        assertEquals(Optional.of(pixel2), dao.getMax());
        verifyParseProduct(mockMax, 2);

        assertEquals(Optional.of((long) products.size() + 1L), dao.getCount());
        verify(mockCount, times(2)).getLong(1);

        assertEquals(Optional.of(sum + pixel2.getPrice()), dao.getSum());
        verify(mockSum, times(2)).getLong(1);
    }

    private void verifyParseProduct(ResultSet mock, int times) throws SQLException {
        verify(mock, times(times)).getString("name");
        verify(mock, times(times)).getLong("price");
    }

    private Product getMaxProduct() {
        return list.stream().max(Comparator.comparing(Product::getPrice)).get();
    }

    private Product getMinProduct() {
        return list.stream().min(Comparator.comparing(Product::getPrice)).get();
    }
}

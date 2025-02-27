import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.UUID;

class Main {
    private static final Logger log = Logger.getLogger(Main.class.getName());

    private static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    private record Obj(String reqInfo, HttpRequest request, int status) {
    }

    enum OrderStatus {CONFIRMED, PENDING}

    record Inventory(String name, int qty) {
        public String json() {
            return String.format("{\"name\":\"%s\",\"qty\":%d}", name, qty);
        }
    }

    record Order(String productId, int qty, OrderStatus status) {
        public String json() {
            return String.format("{\"product_id\":\"%s\",\"qty\":%d,\"status\":\"%s\"}", productId, qty, status);
        }
    }

    private static final Function<String, HttpRequest> get = (url) ->
            HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(3))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

    private static final BiFunction<String, String, HttpRequest> post = (url, body) ->
            HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(3))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();

    private static final Function<String, Obj[]> tests = (port) -> {
        final String inventory = "http://localhost:%s/api/v1/inventory".formatted(port);
        final String order = "http://localhost:%s/api/v1/order".formatted(port);
        // when in e2e profile, dummy inventory with the uuid below is saved to the db
        // see resources/db/data.sql for more info
        final String uuid = "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454";

        return new Obj[] {
                // inventory
                new Obj("should return all inventory/products", get.apply(inventory + "/all"), 200),
                new Obj("should return product by id", get.apply(inventory + "/" + uuid), 200),
                new Obj("fail. product by id does not exist", get.apply(inventory + "/" + UUID.randomUUID().toString()), 404),
                new Obj("should create inventory/product", post.apply(inventory, new Inventory("product", 10).json()), 201),
                new Obj("reject inventory/product creation duplicate", post.apply(inventory, new Inventory("product", 10).json()), 409),

                // order
                new Obj("should return all orders", get.apply(order), 200),
                new Obj("reject order creation. invalid product id", post.apply(order, new Order(UUID.randomUUID().toString(), 1, OrderStatus.CONFIRMED).json()), 404),
                new Obj("should return create order", post.apply(order, new Order(uuid, 1, OrderStatus.CONFIRMED).json()), 201),
                new Obj("reject order creation. qty is greater than inventory qty", post.apply(order, new Order(uuid, 3, OrderStatus.PENDING).json()), 409),
                new Obj("success. inventory qty is now 0", post.apply(order, new Order(uuid, 1, OrderStatus.PENDING).json()), 201),
                new Obj("reject order creation. inventory out of stock", post.apply(order, new Order(uuid, 1, OrderStatus.PENDING).json()), 400),
        };
    };


    public static void main(final String[] args) throws IOException, InterruptedException {
        log.log(Level.INFO, "beginning e2e test");
        for (final Obj obj : tests.apply(args != null && args.length > 0 ? args[0] : "4000")) {
            final var response = client.send(obj.request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != obj.status) {
                log.log(Level.WARNING, obj.reqInfo);
                String format = String.format("\n\nREQUEST FAILED \ntest name: %s,\nerror code: %d,\nrequest body %s",
                        obj.reqInfo, response.statusCode(), response.body());
                throw new RuntimeException(format);
            }

            log.log(Level.INFO, "{0} success", obj.reqInfo);
        }

        log.log(Level.INFO, "e2e test completed");
    }
}

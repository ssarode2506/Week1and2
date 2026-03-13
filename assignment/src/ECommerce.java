import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ECommerce {


        private final ConcurrentHashMap<String, AtomicInteger> stockMap;
        private final ConcurrentHashMap<String, LinkedBlockingQueue<Integer>> waitingListMap;

        public ECommerce() {
            this.stockMap = new ConcurrentHashMap<>();
            this.waitingListMap = new ConcurrentHashMap<>();
        }

        public void addProduct(String productId, int initialStock) {
            stockMap.put(productId, new AtomicInteger(initialStock));
            waitingListMap.put(productId, new LinkedBlockingQueue<>());
        }

        public String checkStock(String productId) {
            AtomicInteger stock = stockMap.get(productId);
            if (stock == null) {
                return "Product not found";
            }
            return stock.get() + " units available";
        }

        public String purchaseItem(String productId, Integer userId) {
            AtomicInteger stock = stockMap.get(productId);
            if (stock == null) {
                return "Product not found";
            }
            int remainingStock = stock.decrementAndGet();
            if (remainingStock >= 0) {
                return "Success, " + remainingStock + " units remaining";
            } else {

                stock.incrementAndGet();
                waitingListMap.get(productId).add(userId);
                int position = waitingListMap.get(productId).size();
                return "Added to waiting list, position #" + position;
            }
        }

        public static void main(String[] args) {
            ECommerce manager = new ECommerce();
            String productId = "IPHONE15_256GB";
            manager.addProduct(productId, 100);

            System.out.println("checkStock(\"IPHONE15_256GB\") -> " + manager.checkStock(productId));

            for (int i = 1; i <= 100; i++) {
                System.out.println("purchaseItem(\"IPHONE15_256GB\", userId=" + i + ") -> " + manager.purchaseItem(productId, i));
            }

            System.out.println("After 100 purchases, checkStock -> " + manager.checkStock(productId));

            System.out.println("purchaseItem(\"IPHONE15_256GB\", userId=99999) -> " + manager.purchaseItem(productId, 99999));
        }
    }


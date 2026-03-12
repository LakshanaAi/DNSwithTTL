import java.util.*;

/**
 * UseCase2CDNDNSCache
 * Simulates CDN edge server DNS caching with TTL.
 */

class CDNEntry {

    String domain;
    String ipAddress;
    long expiryTime;

    public CDNEntry(String domain, String ipAddress, int ttlSeconds) {
        this.domain = domain;
        this.ipAddress = ipAddress;
        this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000);
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}

class CDNDNSCache {

    private HashMap<String, CDNEntry> cache = new HashMap<>();

    private int hits = 0;
    private int misses = 0;

    public String resolve(String domain) {

        CDNEntry entry = cache.get(domain);

        if (entry != null && !entry.isExpired()) {
            hits++;
            System.out.println("resolve(\"" + domain + "\") → CDN Cache HIT → " + entry.ipAddress);
            return entry.ipAddress;
        }

        if (entry != null && entry.isExpired()) {
            System.out.println("resolve(\"" + domain + "\") → CDN Cache EXPIRED");
            cache.remove(domain);
        }

        misses++;

        String ip = queryUpstreamDNS(domain);

        cache.put(domain, new CDNEntry(domain, ip, 10)); // TTL 10 seconds

        System.out.println("resolve(\"" + domain + "\") → CDN Cache MISS → Query upstream → " + ip);

        return ip;
    }

    // Simulated upstream DNS query
    private String queryUpstreamDNS(String domain) {

        Random rand = new Random();

        return "192.168.1." + rand.nextInt(255);
    }

    public void getCacheStats() {

        int total = hits + misses;

        double hitRate = total == 0 ? 0 : (hits * 100.0 / total);

        System.out.println("\nCDN DNS Cache Statistics:");
        System.out.println("Hits: " + hits);
        System.out.println("Misses: " + misses);
        System.out.println("Hit Rate: " + String.format("%.2f", hitRate) + "%");
    }
}

public class DNSwithTTL {

    public static void main(String[] args) throws InterruptedException {

        CDNDNSCache cache = new CDNDNSCache();

        System.out.println("===== CDN Edge DNS Cache =====");

        cache.resolve("netflix.com");
        cache.resolve("netflix.com"); // cache hit

        cache.resolve("youtube.com");

        Thread.sleep(11000); // wait for TTL expiry

        cache.resolve("netflix.com"); // expired entry

        cache.getCacheStats();
    }
}


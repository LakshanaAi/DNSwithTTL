import java.util.*;

/**
 * UseCase1BrowserDNSCache
 * Simulates browser DNS caching with TTL.
 */

class DNSEntry {

    String domain;
    String ipAddress;
    long expiryTime;

    public DNSEntry(String domain, String ipAddress, int ttlSeconds) {
        this.domain = domain;
        this.ipAddress = ipAddress;
        this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000);
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}

class BrowserDNSCache {

    private HashMap<String, DNSEntry> cache = new HashMap<>();

    private int hits = 0;
    private int misses = 0;

    public String resolve(String domain) {

        DNSEntry entry = cache.get(domain);

        if (entry != null && !entry.isExpired()) {
            hits++;
            System.out.println("resolve(\"" + domain + "\") → Cache HIT → " + entry.ipAddress);
            return entry.ipAddress;
        }

        if (entry != null && entry.isExpired()) {
            System.out.println("resolve(\"" + domain + "\") → Cache EXPIRED");
            cache.remove(domain);
        }

        misses++;

        String ip = queryUpstreamDNS(domain);

        cache.put(domain, new DNSEntry(domain, ip, 5)); // TTL = 5 seconds

        System.out.println("resolve(\"" + domain + "\") → Cache MISS → Query upstream → " + ip);

        return ip;
    }

    // Simulated upstream DNS query
    private String queryUpstreamDNS(String domain) {

        Random rand = new Random();

        return "172.217.14." + rand.nextInt(255);
    }

    public void getCacheStats() {

        int total = hits + misses;

        double hitRate = total == 0 ? 0 : (hits * 100.0 / total);

        System.out.println("\nBrowser DNS Cache Statistics:");
        System.out.println("Hits: " + hits);
        System.out.println("Misses: " + misses);
        System.out.println("Hit Rate: " + String.format("%.2f", hitRate) + "%");
    }
}

public class DNSwithTTL {

    public static void main(String[] args) throws InterruptedException {

        BrowserDNSCache cache = new BrowserDNSCache();

        System.out.println("===== Browser DNS Cache =====");

        cache.resolve("google.com");
        cache.resolve("google.com"); // cache hit

        Thread.sleep(6000); // wait for TTL expiry

        cache.resolve("google.com"); // cache expired

        cache.resolve("github.com");

        cache.getCacheStats();
    }
}

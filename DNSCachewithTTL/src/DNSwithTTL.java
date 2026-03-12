import java.util.*;

/**
 * UseCase3CorporateDNSCache
 * Simulates DNS caching in a corporate network server with TTL.
 */

class CorporateDNSEntry {

    String domain;
    String ipAddress;
    long expiryTime;

    public CorporateDNSEntry(String domain, String ipAddress, int ttlSeconds) {
        this.domain = domain;
        this.ipAddress = ipAddress;
        this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000);
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}

class CorporateDNSCache {

    private HashMap<String, CorporateDNSEntry> cache = new HashMap<>();
    private int hits = 0;
    private int misses = 0;

    public String resolve(String domain) {

        CorporateDNSEntry entry = cache.get(domain);

        if (entry != null && !entry.isExpired()) {
            hits++;
            System.out.println("resolve(\"" + domain + "\") → Corporate Cache HIT → " + entry.ipAddress);
            return entry.ipAddress;
        }

        if (entry != null && entry.isExpired()) {
            System.out.println("resolve(\"" + domain + "\") → Cache EXPIRED");
            cache.remove(domain);
        }

        misses++;

        String ip = queryUpstreamDNS(domain);

        cache.put(domain, new CorporateDNSEntry(domain, ip, 15)); // TTL = 15 seconds

        System.out.println("resolve(\"" + domain + "\") → Cache MISS → Query upstream → " + ip);

        return ip;
    }

    // Simulated upstream DNS lookup
    private String queryUpstreamDNS(String domain) {

        Random rand = new Random();
        return "10.0.0." + rand.nextInt(255);
    }

    public void getCacheStats() {

        int total = hits + misses;
        double hitRate = total == 0 ? 0 : (hits * 100.0 / total);

        System.out.println("\nCorporate DNS Cache Statistics:");
        System.out.println("Hits: " + hits);
        System.out.println("Misses: " + misses);
        System.out.println("Hit Rate: " + String.format("%.2f", hitRate) + "%");
    }
}

public class DNSwithTTL {

    public static void main(String[] args) throws InterruptedException {

        CorporateDNSCache cache = new CorporateDNSCache();

        System.out.println("===== Corporate Network DNS Cache =====");

        cache.resolve("internal.company.com");
        cache.resolve("internal.company.com"); // cache hit
        cache.resolve("mail.company.com");

        Thread.sleep(16000); // wait for TTL expiration

        cache.resolve("internal.company.com"); // expired entry

        cache.getCacheStats();
    }
}

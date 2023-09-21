package raylras.zen.bracket;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

public class RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    static Map<String, Object> queryEntryProperties(String validExpr)
            throws IOException, ExecutionException, InterruptedException {
        return getRemoteService().query(validExpr, true)
                .exceptionally(e -> {
                    invalidateRemoteService();
                    throw new RuntimeException(e);
                }).get();
    }

    public static void shutdown() {
        invalidateRemoteService();
        executorService.shutdownNow();
    }

    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    private static Socket socket;
    private static RemoteService remoteService;

    private static RemoteService getRemoteService() throws IOException {
        if (remoteService != null) {
            return remoteService;
        } else {
            return createRemoteService();
        }
    }

    private static RemoteService createRemoteService() throws IOException {
        socket = new Socket("127.0.0.1", 6489);
        Launcher<RemoteService> launcher = Launcher.createLauncher(new Object(), RemoteService.class, socket.getInputStream(), socket.getOutputStream(), executorService, Function.identity());
        launcher.startListening();
        remoteService = launcher.getRemoteProxy();
        return remoteService;
    }

    private static void invalidateRemoteService() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                logger.error("Failed to close socket: {}", socket, e);
            } finally {
                socket = null;
            }
        }
        remoteService = null;
    }

    private interface RemoteService {
        @JsonRequest
        CompletableFuture<Map<String, Object>> query(String validExpr, boolean extras);
    }

}

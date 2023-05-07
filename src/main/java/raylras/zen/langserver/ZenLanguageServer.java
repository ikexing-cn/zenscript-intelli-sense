package raylras.zen.langserver;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.*;
import raylras.zen.code.CompilationUnit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ZenLanguageServer implements LanguageServer, LanguageClientAware {

    public ZenLanguageService service;
    public LanguageClient client;

    public ZenLanguageServer() {
        this.service = new ZenLanguageService(this);
    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        ServerCapabilities capabilities = new ServerCapabilities();
        capabilities.setTextDocumentSync(TextDocumentSyncKind.Full);
        capabilities.setCompletionProvider(new CompletionOptions());
//        capabilities.setDocumentSymbolProvider(true);
//        capabilities.setWorkspaceSymbolProvider(true);
//        capabilities.setDocumentHighlightProvider(true);
//        SignatureHelpOptions signatureHelpOptions = new SignatureHelpOptions();
//        signatureHelpOptions.setTriggerCharacters(Arrays.asList("(", ","));
//        capabilities.setSignatureHelpProvider(signatureHelpOptions);
//        capabilities.setSemanticTokensProvider(new SemanticTokensWithRegistrationOptions(Semantics.SEMANTIC_TOKENS_LEGEND, true));
//        capabilities.setReferencesProvider(true);
//        capabilities.setDeclarationProvider(true);
//        capabilities.setDefinitionProvider(true);
//        capabilities.setTypeDefinitionProvider(true);
//        capabilities.setHoverProvider(true);
//        capabilities.setRenameProvider(true);
        return CompletableFuture.completedFuture(new InitializeResult(capabilities));
    }

    @Override
    public void initialized(InitializedParams params) {
        startListeningFileChanges();
        log("ZenScript Language Server initialized");
    }

    @Override
    public TextDocumentService getTextDocumentService() {
        return service;
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        return service;
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        return new CompletableFuture<>();
    }

    @Override
    public void exit() {
        System.exit(0);
    }

    @Override
    public void connect(LanguageClient client) {
        this.client = client;
    }

    public void log(String message) {
        if (client == null) return;
        client.logMessage(new MessageParams(MessageType.Log, "[info] [Server] " + message));
    }

    private void startListeningFileChanges() {
        List<FileSystemWatcher> watchers = new ArrayList<>(1);
        watchers.add(new FileSystemWatcher(Either.forLeft("**/*" + CompilationUnit.FILE_EXTENSION), WatchKind.Create + WatchKind.Change + WatchKind.Delete));
        Object options = new DidChangeWatchedFilesRegistrationOptions(watchers);
        Registration registration = new Registration(UUID.randomUUID().toString(), "workspace/didChangeWatchedFiles", options);
        client.registerCapability(new RegistrationParams(Collections.singletonList(registration)));
    }

}

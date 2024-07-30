package com.nasva.crptapi;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.springframework.http.MediaType.APPLICATION_JSON;

public class CrptApi {
    private final RequestLimiter requestLimiter;
    private final RestClient restClient;

    public CrptApi(RequestLimiter requestLimiter, String baseUrl) {
        this.requestLimiter = requestLimiter;
        this.restClient = RestClient.create(baseUrl);

    }

    public void createDocument(Document document) throws InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextTime = requestLimiter.nextTime(now);
        if (now.isBefore(nextTime)) {
            wait(Duration.between(now, nextTime).toMillis());
            System.out.println("wait");
        }
        ResponseEntity<Void> response = restClient.post()
                .uri(uriBuilder -> uriBuilder.path("/v3/lk/documents/create").build())
                .contentType(APPLICATION_JSON)
                .body(document)
                .retrieve()
                .toBodilessEntity();
    }

    interface RequestLimiter {
        public LocalDateTime nextTime(LocalDateTime now);
    }

    static class InMemoryRequestLimiter implements RequestLimiter {
        private final int requestLimit;
        private final TimeUnit timeUnit;

        private LinkedList<LocalDateTime> times;

        public InMemoryRequestLimiter(int requestLimit, TimeUnit timeUnit) {
            this.requestLimit = requestLimit;
            this.timeUnit = timeUnit;
            times = new LinkedList<>();
        }

        @Override
        public synchronized LocalDateTime nextTime(LocalDateTime now) {
            if (times.size() >= requestLimit) {
                LocalDateTime nextTime = times.getFirst().plus(1, timeUnit.toChronoUnit());
                times.removeFirst();
                times.add(nextTime);
                return nextTime;
            } else {
                times.add(now);
                return now;
            }
        }
    }


    static class Document{

        private Description description;
        private String docId;
        private String docStatus;
        private DocTypes docType;
        private Boolean importRequest;
        private String ownerInn;
        private String participantInn;
        private String producerInn;
        private LocalDate productionDate;
        private String productionType;
        private List<Product> products;
        private LocalDate regDate;
        private String regNumber;

        public Description getDescription() {
            return description;
        }

        public void setDescription(Description description) {
            this.description = description;
        }

        public String getDocId() {
            return docId;
        }

        public void setDocId(String docId) {
            this.docId = docId;
        }

        public String getDocStatus() {
            return docStatus;
        }

        public void setDocStatus(String docStatus) {
            this.docStatus = docStatus;
        }

        public DocTypes getDocType() {
            return docType;
        }

        public void setDocType(DocTypes docType) {
            this.docType = docType;
        }

        public Boolean getImportRequest() {
            return importRequest;
        }

        public void setImportRequest(Boolean importRequest) {
            this.importRequest = importRequest;
        }

        public String getOwnerInn() {
            return ownerInn;
        }

        public void setOwnerInn(String ownerInn) {
            this.ownerInn = ownerInn;
        }

        public String getParticipantInn() {
            return participantInn;
        }

        public void setParticipantInn(String participantInn) {
            this.participantInn = participantInn;
        }

        public String getProducerInn() {
            return producerInn;
        }

        public void setProducerInn(String producerInn) {
            this.producerInn = producerInn;
        }

        public LocalDate getProductionDate() {
            return productionDate;
        }

        public void setProductionDate(LocalDate productionDate) {
            this.productionDate = productionDate;
        }

        public String getProductionType() {
            return productionType;
        }

        public void setProductionType(String productionType) {
            this.productionType = productionType;
        }

        public List<Product> getProducts() {
            return products;
        }

        public void setProducts(List<Product> products) {
            this.products = products;
        }

        public LocalDate getRegDate() {
            return regDate;
        }

        public void setRegDate(LocalDate regDate) {
            this.regDate = regDate;
        }

        public String getRegNumber() {
            return regNumber;
        }

        public void setRegNumber(String regNumber) {
            this.regNumber = regNumber;
        }

        static class Description{
            private String participantInn;

            public String getParticipantInn() {
                return participantInn;
            }

            public void setParticipantInn(String participantInn) {
                this.participantInn = participantInn;
            }
        }

        enum  DocTypes{
            LP_INTRODUCE_GOODS
        }
        static class Product{

            private String certificateDocument;
            private LocalDate certificateDocumentDate;
            private String certificateDocumentNumber;
            private String ownerInn;
            private String producerInn;
            private LocalDate productionDate;
            private String tnvedCode;
            private String uitCode;
            private String uituCode;


            public String getCertificateDocument() {
                return certificateDocument;
            }

            public void setCertificateDocument(String certificateDocument) {
                this.certificateDocument = certificateDocument;
            }

            public LocalDate getCertificateDocumentDate() {
                return certificateDocumentDate;
            }

            public void setCertificateDocumentDate(LocalDate certificateDocumentDate) {
                this.certificateDocumentDate = certificateDocumentDate;
            }

            public String getCertificateDocumentNumber() {
                return certificateDocumentNumber;
            }

            public void setCertificateDocumentNumber(String certificateDocumentNumber) {
                this.certificateDocumentNumber = certificateDocumentNumber;
            }

            public String getOwnerInn() {
                return ownerInn;
            }

            public void setOwnerInn(String ownerInn) {
                this.ownerInn = ownerInn;
            }

            public String getProducerInn() {
                return producerInn;
            }

            public void setProducerInn(String producerInn) {
                this.producerInn = producerInn;
            }

            public LocalDate getProductionDate() {
                return productionDate;
            }

            public void setProductionDate(LocalDate productionDate) {
                this.productionDate = productionDate;
            }

            public String getTnvedCode() {
                return tnvedCode;
            }

            public void setTnvedCode(String tnvedCode) {
                this.tnvedCode = tnvedCode;
            }

            public String getUitCode() {
                return uitCode;
            }

            public void setUitCode(String uitCode) {
                this.uitCode = uitCode;
            }

            public String getUituCode() {
                return uituCode;
            }

            public void setUituCode(String uituCode) {
                this.uituCode = uituCode;
            }
        }
    }
}

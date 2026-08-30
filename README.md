# CloudPulse — Order Management Microservice with Full-Stack Observability

> 🚧 **Project status:** Core service + local observability stack functional. AWS/Azure cloud deployment in progress — see [Roadmap](#roadmap) below.

A Spring Boot microservice demonstrating an end-to-end cloud-native delivery pipeline: containerized with Docker, deployed to **AWS EKS** (with a documented **Azure AKS** alternate path), instrumented with **Prometheus + Grafana**, and shipped through a **GitHub Actions CI/CD pipeline** with automated testing and container vulnerability scanning.

Built to go deeper than a typical fresher project :) — real Infrastructure as Code, real metrics on real business events, not just a "hello world" wrapped in Docker.

![Architecture Diagram](docs/architecture-diagram.svg)


## Tech stack

| Layer | Technologies |
|---|---|
| **Application** | Java 17, Spring Boot 3, Spring Data JPA, Spring Validation |
| **Database** | PostgreSQL (Amazon RDS in cloud, containerized locally) |
| **Observability** | Micrometer, Prometheus, Grafana (custom dashboards + business metrics) |
| **Containerization** | Docker, multi-stage builds, non-root runtime user |
| **Orchestration** | Kubernetes, Amazon EKS, Horizontal Pod Autoscaler, ALB Ingress Controller |
| **Infrastructure as Code** | Terraform (AWS: VPC/EKS/ECR/RDS · Azure: Resource Group/AKS/ACR) |
| **CI/CD** | GitHub Actions — build, test, Trivy security scan, push to ECR, deploy to EKS |
| **API Docs** | springdoc-openapi (Swagger UI) |

## Architecture

```
Developer push → GitHub Actions (build/test/scan) → Amazon ECR
                                                          │
                                                          ▼
                              AWS VPC ── Amazon EKS Cluster
                              ├── order-service pods (2-6, HPA-managed)
                              ├── ALB Ingress (internet-facing)
                              ├── kube-prometheus-stack (Prometheus + Grafana)
                              └── connects to → Amazon RDS (PostgreSQL, private subnet)
```

Metrics flow: the app exposes `/actuator/prometheus` via Micrometer → Prometheus scrapes it every 5-10s → Grafana dashboards visualize JVM health, HTTP latency (p95/p99), and custom business metrics (orders created/failed/cancelled, processing duration).

The same Docker image and Kubernetes manifests deploy unmodified to **Azure AKS** — see `infra/azure/`.

## Project structure

```
cloudpulse-order-service/
├── src/main/java/com/cloudpulse/orderservice/
│   ├── controller/       # REST API endpoints
│   ├── service/          # Business logic + metrics instrumentation
│   ├── repository/       # Spring Data JPA repositories
│   ├── model/             # Entities and DTOs
│   ├── config/            # Custom Micrometer metric definitions
│   └── exception/         # Centralized error handling
├── monitoring/
│   ├── prometheus.yml               # Local scrape config (docker-compose)
│   └── grafana/dashboards/          # Pre-built custom dashboard JSON
├── k8s/
│   ├── deployment.yaml, service.yaml, ingress.yaml, hpa.yaml
│   ├── servicemonitor.yaml          # Auto-discovery for cluster Prometheus
│   └── helm-values/                 # kube-prometheus-stack overrides
├── infra/
│   ├── aws/                # Terraform: VPC, EKS, ECR, RDS
│   └── azure/              # Terraform: Resource Group, AKS, ACR (alternate path)
├── .github/workflows/ci-cd.yml
├── Dockerfile
└── docker-compose.yml       # Full local stack: app + Postgres + Prometheus + Grafana
```

## Running locally

```bash
git clone https://github.com/<your-username>/cloudpulse-order-service.git
cd cloudpulse-order-service
docker-compose up --build
```

- API: `http://localhost:8080/api/v1/orders`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000` (login `admin` / `admin`)

Full step-by-step walkthrough — including how to load-test it and what to expect on the dashboards — is in [`docs/SETUP_GUIDE.md`](docs/SETUP_GUIDE.md).

## Deploying to AWS

```bash
cd infra/aws
terraform init
terraform apply -var="db_password=<your-secure-password>"

# once applied:
aws eks update-kubeconfig --region ap-south-1 --name cloudpulse-dev-eks
kubectl apply -f ../../k8s/
```

Full walkthrough, including the Helm install for the monitoring stack, is in [`docs/SETUP_GUIDE.md`](docs/SETUP_GUIDE.md).

## Sample API usage

```bash
# Create an order
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{"customerName": "Asha Verma", "productName": "Wireless Mouse", "quantity": 2}'

# List all orders
curl http://localhost:8080/api/v1/orders

# Update status
curl -X PATCH http://localhost:8080/api/v1/orders/1/status \
  -H "Content-Type: application/json" \
  -d '{"status": "SHIPPED"}'
```

## What the Grafana dashboard tracks

- HTTP request rate and p95/p99 latency per endpoint
- JVM heap usage and garbage collection pause time
- **Custom business metrics**: `orders_created_total`, `orders_failed_total`, `orders_cancelled_total`, `orders_processing_duration_seconds`

## Roadmap

- [x] Core Spring Boot service with validation, exception handling, and OpenAPI docs
- [x] Micrometer + Prometheus instrumentation with custom business metrics
- [x] Dockerized local stack (app + Postgres + Prometheus + Grafana)
- [x] Kubernetes manifests + Terraform for AWS EKS
- [x] GitHub Actions CI/CD pipeline with Trivy vulnerability scanning
- [x] Terraform for Azure AKS as an alternate deployment target
- [ ] Live AWS deployment with public demo URL
- [ ] Load testing results (k6) documented with before/after autoscaling screenshots
- [ ] Alertmanager → Slack integration for threshold-based alerts

## License

MIT — built as a personal portfolio/learning project.

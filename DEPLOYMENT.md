# Deployment Guide

This guide explains how to deploy the Customer API using GitHub Actions, Quay.io, and ArgoCD.

## CI/CD Pipeline

### GitHub Actions Workflow

The GitHub Actions workflow (`.github/workflows/build-and-push.yml`) automatically:

1. **Builds a native executable** using Quarkus container build for x86/amd64 architecture
2. **Builds a Docker image** based on UBI9 minimal image
3. **Pushes the image** to Quay.io at `quay.io/mklaasse/quarkus-customer-api`

#### Required GitHub Secrets

Configure the following secrets in your GitHub repository:

- `QUAY_USERNAME`: Your Quay.io username
- `QUAY_PASSWORD`: Your Quay.io password or robot account token

To set up secrets:
1. Go to your GitHub repository
2. Navigate to Settings → Secrets and variables → Actions
3. Add the secrets listed above

#### Workflow Triggers

The workflow runs on:
- Push to `main` branch
- Pull requests to `main` branch
- Manual trigger via `workflow_dispatch`

#### Image Tags

- `latest`: Latest build from main branch
- `main-<sha>`: Tagged with branch name and commit SHA

## Helm Chart

The Helm chart is located in `helm/customer-api/` and includes:

- **Deployment**: Kubernetes deployment with configurable replicas
- **Service**: ClusterIP service exposing the application
- **ServiceAccount**: Service account for the pods
- **Route**: OpenShift Route for external access (edge TLS termination)
- **PostgreSQL**: Built-in PostgreSQL deployment with automatic password generation

### Installing the Helm Chart

```bash
helm install customer-api ./helm/customer-api \
  --set image.tag=latest \
  --set database.enabled=true \
  --set database.host=postgres-service \
  --set database.username=postgres \
  --set database.password=your-password
```

### Customizing Values

Edit `helm/customer-api/values.yaml` or override values during installation:

```bash
helm install customer-api ./helm/customer-api -f custom-values.yaml
```

## ArgoCD Application

The ArgoCD Application manifest is located in `argocd/application.yaml`.

### Prerequisites

1. ArgoCD installed in your Kubernetes cluster
2. Repository accessible by ArgoCD
3. Helm chart in the repository

### Deploying with ArgoCD

1. **Update the repository URL** in `argocd/application.yaml`:
   ```yaml
   source:
     repoURL: https://github.com/YOUR_USERNAME/YOUR_REPO.git
   ```

2. **Apply the ArgoCD Application**:
   ```bash
   kubectl apply -f argocd/application.yaml
   ```

3. **Verify the application** in ArgoCD UI or CLI:
   ```bash
   argocd app get customer-api
   ```

### ArgoCD Application Features

- **Automated sync**: Automatically syncs when the repository changes
- **Self-healing**: Automatically corrects drift
- **Auto-prune**: Removes resources that are no longer in Git
- **Retry logic**: Automatically retries failed syncs

### Updating the Application

To update the deployed application:

1. Update the image tag in `argocd/application.yaml`:
   ```yaml
   values: |
     image:
       tag: "main-abc1234"  # Use specific commit SHA tag
   ```

2. Commit and push the changes
3. ArgoCD will automatically detect and sync the changes

## Database Configuration

The Helm chart includes a built-in PostgreSQL deployment with **automatic password generation** for zero-trust security.

### Automatic Password Generation

The Helm chart automatically generates a secure random password for PostgreSQL at deployment time:

- **First deployment**: Generates a random 32-character alphanumeric password
- **Subsequent upgrades**: Automatically reuses the existing password from the Kubernetes secret
- **No passwords in Git**: The password is never stored in the repository

#### How It Works

1. On first `helm install`, the chart generates a random password using `randAlphaNum 32`
2. The password is stored in a Kubernetes Secret
3. On `helm upgrade`, the chart uses Helm's `lookup` function to retrieve the existing password from the secret
4. The password persists across upgrades, ensuring database continuity

#### Override Password (Optional)

If you need to set a specific password, you can override it in `values.yaml`:

```yaml
postgresql:
  enabled: true
  password: "your-custom-password"  # Only if you need a specific password
```

**Note**: The `lookup` function requires Helm to have access to the Kubernetes API. It works during `helm install` and `helm upgrade`, but not during `helm template` (dry-run mode).

### PostgreSQL Configuration

The default configuration includes:

```yaml
postgresql:
  enabled: true
  database: customerdb
  username: postgres
  # password: ""  # Auto-generated if not specified
  persistence:
    enabled: true
    size: 1Gi
```

The Quarkus application automatically connects to the PostgreSQL service using the generated credentials stored in the Kubernetes secret.

## Verification

After deployment, verify the application:

```bash
# Check pods
kubectl get pods -l app.kubernetes.io/name=customer-api

# Check service
kubectl get svc customer-api

# Check logs
kubectl logs -l app.kubernetes.io/name=customer-api

# Test the API
kubectl port-forward svc/customer-api 8080:80
curl http://localhost:8080/customer
```

## Troubleshooting

### Image Pull Errors

If you see image pull errors:
1. Verify Quay.io credentials in GitHub secrets
2. Check image exists: `docker pull quay.io/mklaasse/quarkus-customer-api:latest`
3. Verify image pull secrets are configured in Kubernetes

### Health Check Failures

The application uses Quarkus health endpoints:
- Liveness: `/q/health/live`
- Readiness: `/q/health/ready`

Ensure `quarkus-smallrye-health` extension is included (already added to `pom.xml`).

### Database Connection Issues

1. Verify database service is accessible
2. Check database credentials
3. Verify network policies allow connection
4. Check application logs for connection errors


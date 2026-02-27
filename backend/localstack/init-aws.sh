#!/bin/bash
set -euo pipefail

echo "Initializing LocalStack AWS resources..."

# S3 Buckets
awslocal s3 mb s3://hotspot-media
awslocal s3 mb s3://hotspot-profile-pictures
echo "S3 buckets created."

# SQS Queues
awslocal sqs create-queue --queue-name hotspot-notifications
awslocal sqs create-queue --queue-name hotspot-email
echo "SQS queues created."

# SNS Topics
awslocal sns create-topic --name hotspot-events
echo "SNS topics created."

# SES - Verify a test email identity
awslocal ses verify-email-identity --email-address noreply@hotspot.local
echo "SES email identity verified."

echo "LocalStack initialization complete."

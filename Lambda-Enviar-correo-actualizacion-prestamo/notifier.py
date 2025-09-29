import boto3

sns = boto3.client('sns')
SNS_TOPIC_ARN = "arn:aws:sns:us-east-1:********:loan-notifications-topic"

def send_notification(message: str):
    sns.publish(
        TopicArn=SNS_TOPIC_ARN,
        Message=message,
        Subject="LoanState Notification"
    )

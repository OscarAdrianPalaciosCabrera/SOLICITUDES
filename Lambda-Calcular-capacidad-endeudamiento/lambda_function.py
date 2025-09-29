import json
import boto3
from loan_processing import evaluate_loan
from calculator import generate_monthly_plan
from build_message import build_payment_plan_message

sqs = boto3.client('sqs')
sns =boto3.client('sns')

# URL de la cola de salida (donde ms-solicitudes escuchará)
OUTPUT_QUEUE_URL = "https://sqs.us-east-1.amazonaws.com/*********/actualizacion-estado"
TOPIC_ARN = "arn:aws:sns:us-east-1:********:loan-notifications-topic"


def lambda_handler(event, context):
    responses = []

    for record in event['Records']:
        try:
            print("Mensaje recibido en la Lambda:", record['body'])

            payload = json.loads(record['body'])
            decision = evaluate_loan(payload)  # 1 = aprobado, 2 = rechazado

            monthly_plan = None
            response = {
                "id": payload.get("id"),
                "decision": decision
            }

            # Solo generar plan si fue aprobado
            if decision == 1:
                new_loan = {
                    "amount": float(payload.get("loanAmount")),
                    "interestRate": float(payload.get("interestRate")) / 100,
                    "timeLimit": int(payload.get("timeLimit"))
                }
                monthly_plan = generate_monthly_plan(new_loan)
                response["monthlyPlan"] = monthly_plan
     
            
            message_toClient = build_payment_plan_message({
                "identityDocument": payload.get("identityDocument"),
                "loanId": payload.get("id"),
                "monthlyPlan": monthly_plan
            })

            print("Mensaje a enviar a SNS:", message_toClient)


            # Enviar mensaje a la cola updateState
            print("Enviando mensaje a SQS:", json.dumps(response))
            sqs.send_message(
                QueueUrl=OUTPUT_QUEUE_URL,
                MessageBody=json.dumps(response, default=str)
            )

            print ("Enviando mensaje a SNS:", message_toClient)
            sns.publish(
                TopicArn=TOPIC_ARN,
                Message=message_toClient,
                Subject="Plan de pagos CrediYa",
                MessageAttributes={
                    "eventType": {
                        "DataType": "String",
                        "StringValue": "LOAN_PAYMENT_PLAN"
                    }
                }
            )

            # Guardar en responses para el log
            responses.append({
                "solicitudId": payload.get("solicitudId"),
                "estado": decision,
                "planGenerated": bool(monthly_plan)
            })
        

        except Exception as e:
            responses.append({
                "error": str(e),
                "record": record['body']
            })

    return {
        "statusCode": 200,
        "body": json.dumps(responses, default=str)
    }

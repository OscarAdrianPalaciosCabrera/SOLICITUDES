import json
from state_mapper import map_state
from message_builder import build_message
from notifier import send_notification

def lambda_handler(event, context):
    for record in event['Records']:
        try:
            print("Processing message from SQS")

            # Mensaje desde SQS
            message = record["body"]
            messagedic = json.loads(message)

            # 1. Mapear estado
            state_str = map_state(messagedic.get("state"))

            # 2. Construir mensaje
            formatted_message = build_message(messagedic, state_str)

            # 3. Publicar en SNS
            send_notification(formatted_message)

        except Exception as e:
            print(f"Error processing message: {e}")
            continue

    return {
        'statusCode': 200,
        'body': json.dumps('Processed messages')
    }

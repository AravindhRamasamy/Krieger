package com.example.Krieger.consumer;

import com.example.Krieger.config.RabbitMQConfig;
import com.example.Krieger.entity.Document;
import com.example.Krieger.repository.AuthorRepository;
import com.example.Krieger.repository.DocumentRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Consumer {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private DocumentRepository documentRepository;

    // Listens for messages from the Queue
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void consumeMessage(String message) {
        System.out.println("Received message: " + message);
        String[] parts = message.split(": ");
        String eventType = parts[0];
        Long authorId = Long.parseLong(parts[1]);

        // If DELETE event type, It removes the author and their associated documents.
        if ("DELETE".equals(eventType)) {
            List<Document> documents = documentRepository.findByAuthorId(authorId);
            documentRepository.deleteAll(documents);
            authorRepository.deleteById(authorId);
            System.out.println("Author and documents deleted");
        }
    }
}

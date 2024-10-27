package Services;

import Domain.Ticket;
import Repository.TicketRepository;

import java.sql.SQLException;
import Exception.ValidationException;

public class TicketService {
    private TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public Ticket getTicket(int ticketId) throws SQLException, ValidationException {

        if(ticketId == 0){
            throw new ValidationException("ticketId is required");
        }

        Ticket ticket = ticketRepository.findById(ticketId);

        if(ticket == null){
            throw new ValidationException("Ticket not found");
        }
        return ticket;
    }

}

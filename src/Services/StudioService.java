package Services;

import Config.Database;
import Domain.Studio;
import Model.*;
import Repository.StudioRepository;
import Exception.ValidationException;

import java.sql.SQLException;

public class StudioService {

    private StudioRepository studioRepository;

    public StudioService (StudioRepository studioRepository){
        this.studioRepository = studioRepository;
    }

    public AddStudioResponse add(AddStudioRequest request) throws ValidationException , SQLException {
        ValidateAddStudioRequest(request);

        try{
            Database.beginTransaction();

            if(this.studioRepository.find(request.id) != null){
                throw new ValidationException("Studio already exists");
            }

            Studio studio = new Studio();
            studio.id = request.id;
            studio.name = request.name;
            studio.capacity = request.capacity;

            Studio row = this.studioRepository.save(studio);

            Database.commitTransaction();

            AddStudioResponse response = new AddStudioResponse();
            response.studio = row;
            return response;

        }catch (SQLException e){
            Database.rollbackTransaction();
            throw e;
        }

    }

    private void ValidateAddStudioRequest(AddStudioRequest request) throws ValidationException {
        if(request.id < 0 || request.name == null || request.name.isEmpty() || request.capacity < 0 ){
            throw new ValidationException("ID ,name,capacity are required");
        }
    }

    public UpdateStudioResponse update(UpdateStudioRequest request) throws ValidationException , SQLException {
        ValidateUpdateStudioRequest(request);

        try{
            Database.beginTransaction();

            if(this.studioRepository.find(request.id) == null){
                throw new ValidationException("Studio not found");
            }

            Studio studio = new Studio();
            studio.id = request.id;
            studio.name = request.name;
            studio.capacity = request.capacity;

            Studio row = this.studioRepository.update(studio);

            Database.commitTransaction();

            UpdateStudioResponse response = new UpdateStudioResponse();
            response.studio = row;
            return response;

        }catch (SQLException e){
            Database.rollbackTransaction();
            throw e;
        }
    }

    private void ValidateUpdateStudioRequest(UpdateStudioRequest request) throws ValidationException {
        if(request.id < 0 || request.name == null || request.name.isEmpty() || request.capacity < 0 ){
            throw new ValidationException("ID ,name,capacity are required");
        }
    }

    public void delete(DeleteStudioRequest request) throws ValidationException , SQLException {
        if(request.id < 0){
            throw new ValidationException("ID is required");
        }

        try {

            Database.beginTransaction();

            if(this.studioRepository.find(request.id) == null){
                throw new ValidationException("Studio not found");
            }

            this.studioRepository.delete(request.id);

            Database.commitTransaction();

        }catch (SQLException e){
            Database.rollbackTransaction();
            throw e;
        }

    }

    public Studio findById(int studioId) throws SQLException, ValidationException {
        Studio studio = this.studioRepository.find(studioId);
        if(studio == null){
            throw new ValidationException("Studio not found");
        }
        return studio;
    }

    public FindAllStudioResponse showAll() throws SQLException {
        Studio[] studios = this.studioRepository.findAll();
        FindAllStudioResponse response = new FindAllStudioResponse();
        response.studios = studios;
        return response;
    }
}

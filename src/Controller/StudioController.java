package Controller;

import Config.Database;
import Domain.Studio;
import Model.*;
import Repository.StudioRepository;
import Services.StudioService;

import java.sql.Connection;
import java.sql.SQLException;

import Exception.ValidationException;

public class StudioController {
    private StudioService studioService;

    public StudioController() {
        Connection connection = Database.getConnection();
        StudioRepository studioRepository = new StudioRepository(connection);
        this.studioService = new StudioService(studioRepository);
    }

    public void showAll() {
        try {
            FindAllStudioResponse response = this.studioService.showAll();
            Studio[] studios = response.studios;

//            gunakan view objek untuk menampilkan semua studio
//            gunakan looping untuk menampilkan semua studio
//            for (Studio studio : studios) {
//            System.out.println(studio.id + " " + studio.name + " " + studio.capacity);
//            }


        } catch (SQLException err) {
//            hanlde error memakai tampilan atau alert

            System.out.println(err.getMessage());
        }
    }

    public void addStudio() {
        try {
            int id = 0;
            String name = "";
            int capacity = 0;

//          tampilkan form untuk menambahkan studio
//          masukan data data ke variabel diatas


            AddStudioRequest request = new AddStudioRequest();
            request.id = id;
            request.name = name;
            request.capacity = capacity;

            AddStudioResponse response = this.studioService.add(request);
            Studio studio = response.studio;

//            gunakan view objek untuk menampilkan studio yang baru saja ditambahkan
//            System.out.println("Studio " + studio.name + " berhasil ditambahkan");

        } catch (ValidationException | SQLException err) {
//            hanlde error memakai tampilan atau alert
            System.out.println(err.getMessage());
        }
    }

    public void updateStudio() {
        try {
            int id = 0;
            String name = "";
            int capacity = 0;

//          tampilkan form untuk mengupdate studio
//          masukan data data ke variabel diatas

            UpdateStudioRequest request = new UpdateStudioRequest();
            request.id = id;
            request.name = name;
            request.capacity = capacity;

            UpdateStudioResponse response = this.studioService.update(request);
            Studio studio = response.studio;

//
//            gunakan view objek untuk menampilkan studio yang baru saja diupdate
//            System.out.println("Studio " + studio.name + " berhasil diupdate");

        } catch (ValidationException | SQLException err) {
//            handle error dengan alert atau tampilan
            System.out.println(err.getMessage());
        }
    }

    public void deleteStudio() {
        try {
            int id = 0;

//            tampilkan form untuk menghapus studio
//            masukan data data ke variabel diatas

            DeleteStudioRequest request = new DeleteStudioRequest();
            request.id = id;

            this.studioService.delete(request);

//            gunakan view objek untuk menampilkan studio yang baru saja dihapus
//            System.out.println("Studio berhasil dihapus");

        } catch (SQLException | ValidationException err) {
//            handle error dengan alert atau tampilan
            System.out.println(err.getMessage());
        }
    }
}

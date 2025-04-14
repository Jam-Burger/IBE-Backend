package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.model.dto.out.TaskDetailsDTO;
import com.kdu.hufflepuff.ibe.model.entity.CleanTask;
import com.kdu.hufflepuff.ibe.model.graphql.Room;
import com.kdu.hufflepuff.ibe.repository.jpa.CleanTaskRepository;
import com.kdu.hufflepuff.ibe.service.interfaces.AdminService;
import com.kdu.hufflepuff.ibe.service.interfaces.EmailService;
import com.kdu.hufflepuff.ibe.service.interfaces.TaskService;
import com.kdu.hufflepuff.ibe.util.GraphQLQueries;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final CleanTaskRepository cleanTaskRepository;
    private final ModelMapper modelMapper;
    private final GraphQlClient graphQlClient;
    private final AdminService adminService;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;


    @Override
    public List<TaskDetailsDTO> getAllTasksOfToday(Long propertyId) {
        validateAdminAccess(propertyId);
        LocalDate today = LocalDate.now();
        List<CleanTask> cleanTasks = cleanTaskRepository.findByDateAndStaff_PreferredShift_PropertyId(today, propertyId);
        return mapCleanTasksToTaskDetailsDTOS(cleanTasks);
    }

    private Map<Long, Integer> getRoomNumberMap(List<CleanTask> cleanTasks) {
        List<Long> roomIds = cleanTasks.stream()
            .map(CleanTask::getRoomId)
            .distinct()
            .toList();

        Map<Long, Integer> roomNumberMap = new HashMap<>();

        List<Room> rooms = graphQlClient.document(GraphQLQueries.GET_ROOMS_FROM_ROOM_IDS)
            .variable("roomIds", roomIds)
            .retrieve("listRooms")
            .toEntityList(Room.class)
            .block();

        if (rooms == null) {
            return roomNumberMap;
        }

        rooms.forEach(room -> roomNumberMap.put(room.getRoomId(), room.getRoomNumber()));
        return roomNumberMap;
    }

    private List<TaskDetailsDTO> mapCleanTasksToTaskDetailsDTOS(List<CleanTask> cleanTasks) {
        Map<Long, Integer> roomNumberMap = getRoomNumberMap(cleanTasks);

        return cleanTasks.stream()
            .map(cleanTask -> {
                TaskDetailsDTO taskDetailsDTO = modelMapper.map(cleanTask, TaskDetailsDTO.class);
                taskDetailsDTO.setRoomNumber(roomNumberMap.get(cleanTask.getRoomId()));
                taskDetailsDTO.setEndTime(cleanTask.getStartTime().plusMinutes(cleanTask.getTaskType().getRequiredTime().toSeconds()));
                return taskDetailsDTO;
            })
            .toList();
    }

    private void validateAdminAccess(Long propertyId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        if (adminService.isOfProperty(email, propertyId)) {
            return;
        }

        throw new AccessDeniedException("Unauthorized access");
    }


    @Override
    public void sendSchedulingEmail(Long propertyId) {
        List<TaskDetailsDTO> tasks = getAllTasksOfToday(propertyId);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Context context = new Context();
        context.setVariable("tasks", tasks);
        context.setVariable("title", "Task Report");
        context.setVariable("date", tasks.getFirst().getDate());
        String subject = "Today's Tasks Schedule";

        try {
            emailService.sendEmail(
                email,
                subject,
                "tasks-schedule",
                context
            );
        } catch (MessagingException e) {
            log.error(e.getMessage(), e);
        }
    }
}

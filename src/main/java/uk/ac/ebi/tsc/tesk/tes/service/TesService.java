package uk.ac.ebi.tsc.tesk.tes.service;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import uk.ac.ebi.tsc.tesk.common.data.TaskView;
import uk.ac.ebi.tsc.tesk.common.data.security.User;
import uk.ac.ebi.tsc.tesk.common.data.tes.TesCreateTaskResponse;
import uk.ac.ebi.tsc.tesk.common.data.tes.TesListTasksResponse;
import uk.ac.ebi.tsc.tesk.common.data.tes.TesTask;


/**
 * @author Ania Niewielska <aniewielska@ebi.ac.uk>
 * <p>
 * Implementation of controller endpoints.
 * Orchestrates all kubernetes client calls (via wrapper)
 * and converting inputs and outputs with the help of the converter.
 */
public interface TesService {
    /**
     * Creates new TES task, by converting input and calling create method.
     * In case of detecting duplicated task ID, retries with new generated ID (up to a limit of retries)
     */
    @PreAuthorize("hasRole(@authorisationProperties.baseGroup) AND" +
            "(#user.member OR #user.teskAdmin) AND" +
            "(#task.tags?.get('GROUP_NAME') == null OR #user.isGroupMember(#task.tags['GROUP_NAME']))")
    TesCreateTaskResponse createTask(TesTask task, User user);

    /**
     * Gets single task's details based on ID.
     * Performs a series of kubernetes API calls and converts results with means of the converter.
     *
     * @param taskId - TES task ID (==taskmaster's job name)
     * @param view   - one of {@link TaskView} values, decides on how much detail is put in results
     * @return - TES task details
     */
    @PostAuthorize("(#user.username == returnObject.logs[0].metadata['USER_ID'] AND returnObject.logs[0].metadata['GROUP_NAME'] != null AND #user.isGroupMember(returnObject.logs[0].metadata['GROUP_NAME']))" +
            " OR (#user.teskAdmin)" +
            " OR (returnObject.logs[0].metadata['GROUP_NAME'] != null AND #user.isGroupManager(returnObject.logs[0].metadata['GROUP_NAME']))")
    TesTask getTask(String taskId, TaskView view, User user);

    /**
     * Gets a full list of tasks. Performs Kubernetes API batch calls (all taskmasters, all executors, all pods),
     * combines them together into valid {@link uk.ac.ebi.tsc.tesk.k8s.data.Task} objects and converts to result with means of the converter.
     *
     * @param namePrefix - Unsupported
     * @param pageSize   - Unsupported, in future releases should enable list chunking
     * @param pageToken  - Unsupported, in future releases should enable list chunking
     * @param view       - one of {@link TaskView} values, decides on how much detail is put in each resulting task
     * @return - resulting list of tasks plus paging token (when supported)
     */
    @PreAuthorize("#user.teskAdmin OR #user.manager OR #user.member")
    TesListTasksResponse listTasks(String namePrefix,
                                   Long pageSize,
                                   String pageToken,
                                   TaskView view,
                                   User user);

    /**
     * Cancels a task with a given id, by setting a label to taskmaster Job and Pod object.
     * If not task with a given Id - throws TaskNotFoundException
     * If task completed, throws CancelNotRunningTask
     *
     * @param taskId - TES task ID (==taskmaster's job name)
     */
    @PreAuthorize("hasRole(@authorisationProperties.baseGroup)")
    void cancelTask(String taskId);
}

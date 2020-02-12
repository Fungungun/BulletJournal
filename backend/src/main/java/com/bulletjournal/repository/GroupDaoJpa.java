package com.bulletjournal.repository;

import com.bulletjournal.authz.AuthorizationService;
import com.bulletjournal.authz.ContentType;
import com.bulletjournal.authz.Operation;
import com.bulletjournal.controller.models.AddUserGroupParams;
import com.bulletjournal.controller.models.UpdateGroupParams;
import com.bulletjournal.exceptions.ResourceNotFoundException;
import com.bulletjournal.repository.models.Group;
import com.bulletjournal.repository.models.User;
import com.bulletjournal.repository.models.UserGroup;
import com.bulletjournal.repository.utils.DaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Repository
public class GroupDaoJpa {

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserDaoJpa userDaoJpa;

    @Autowired
    private AuthorizationService authorizationService;

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Group create(String name, String owner) {
        User user = this.userDaoJpa.getByName(owner);
        Group group = new Group();
        group.setName(name);
        group.setOwner(owner);
        group.addUser(user);

        group = this.groupRepository.save(group);
        this.userGroupRepository.save(new UserGroup(user, group, true));
        return group;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void delete(Long groupId, String requester) {

        Group group = this.groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group " + groupId + " not found"));

        this.authorizationService.checkAuthorizedToOperateOnContent(
                group.getOwner(), requester, ContentType.GROUP, Operation.DELETE, groupId, group.getName());

        for (UserGroup userGroup : group.getUsers()) {
            this.userGroupRepository.delete(userGroup);
        }
        this.groupRepository.delete(group);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Group partialUpdate(String requester, Long groupId, UpdateGroupParams updateGroupParams) {
        Group group = this.groupRepository
                .findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group " + groupId + " not found"));

        this.authorizationService.checkAuthorizedToOperateOnContent(
                group.getOwner(), requester, ContentType.GROUP, Operation.UPDATE, groupId);

        DaoHelper.updateIfPresent(
                updateGroupParams.hasName(), updateGroupParams.getName(), (value) -> group.setName(value));

        return this.groupRepository.save(group);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public List<com.bulletjournal.controller.models.Group> getGroups(String owner) {
        User user = this.userDaoJpa.getByName(owner);
        return user.getGroups()
                .stream()
                .map(userGroup -> {
                    com.bulletjournal.controller.models.Group g = userGroup.getGroup().toPresentationModel();
                    g.setUsers(userGroup.getGroup().getUsers()
                            .stream()
                            .map(u ->
                                    new com.bulletjournal.controller.models.UserGroup(
                                            u.getUser().getName(), userGroup.isAccepted()))
                            .collect(Collectors.toList()));
                    return g;
                })
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void addUserGroups(
            String owner,
            List<AddUserGroupParams> addUserGroupsParams) {
        for (AddUserGroupParams addUserGroupParams : addUserGroupsParams) {
            Long groupId = addUserGroupParams.getGroupId();
            Group group = this.groupRepository.findById(groupId)
                    .orElseThrow(() -> new ResourceNotFoundException("Group " + groupId + " not found"));
            this.authorizationService.checkAuthorizedToOperateOnContent(
                    group.getOwner(), owner, ContentType.GROUP, Operation.UPDATE, groupId);
            User user = this.userDaoJpa.getByName(addUserGroupParams.getUsername());
            this.userGroupRepository.save(new UserGroup(user, group, false));
        }
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Group getGroup(Long id) {
        return this.groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group " + id + " not found"));
    }
}
import React from 'react';
import { RouteComponentProps } from 'react-router';
import { connect } from 'react-redux';
import { getGroup } from '../features/group/actions';
import { Group, User } from '../features/group/reducer';
import { IState } from '../store';
import { Icon, Avatar, Button, List, Badge } from 'antd';

type GroupPathParams = {
  groupId: string;
};

interface GroupPathProps extends RouteComponentProps<GroupPathParams> {
  groupId: string;
}

type GroupProps = {
  group: Group;
  getGroup: (groupId: number) => void;
};

function getGroupUserTitle(item: User, group: Group): string {
  if (item.name === group.owner) {
    return 'Owner';
  }
  return item.accepted ? 'Joined' : 'Not Joined';
}

function getGroupUserSpan(item: User, group: Group): JSX.Element {
  if (item.name === group.owner) {
    return (<span>&nbsp;&nbsp;<strong>{item.name}</strong></span>);
  }
  if (item.accepted) {
    return (<span>&nbsp;&nbsp;{item.name}</span>);
  }

  return (<span style={{color:'grey'}}>&nbsp;&nbsp;{item.name}</span>);
}

class GroupPage extends React.Component<GroupProps & GroupPathProps> {
  componentDidMount() {
    const groupId = this.props.match.params.groupId;
    console.log(groupId);
    this.props.getGroup(parseInt(groupId));
  }

  componentDidUpdate(prevProps: GroupPathProps): void {
    const groupId = this.props.match.params.groupId;
    if (groupId !== prevProps.match.params.groupId) {
      this.props.getGroup(parseInt(groupId));
    }
  }

  render() {
    const { group } = this.props;
    return (
      <div className="group-page">
        <div className="group-title">
          <h3>{`Group "${group.name}"`}</h3>
          <Icon type="dash" title="Edit Group" style={{cursor: 'pointer'}}/>
        </div>
        <div className="group-users">
          <List
            dataSource={group.users}
            renderItem={item => {
              return (
                <List.Item key={item.id}>
                  <div className="group-user" title={getGroupUserTitle(item, group)}>
                    <Badge dot={!item.accepted}>
                      <Avatar src={item.avatar} />
                    </Badge>
                    {getGroupUserSpan(item, group)}
                  </div>
                  {item.name !== group.owner && (
                    <Button type="danger" icon="close" ghost size="small" title={item.accepted ? "Remove" : "Cancel Invitation"}/>
                  )}
                </List.Item>
              );
            }}
          />
        </div>
        <div className="group-footer">
          <Button type="primary" icon="plus" shape="round" title="Add User"/>
        </div>
      </div>
    );
  }
}

const mapStateToProps = (state: IState) => ({
  group: state.group.group
});

export default connect(mapStateToProps, { getGroup })(GroupPage);

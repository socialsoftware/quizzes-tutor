describe('Administration walkthrough', () => {
  beforeEach(() => {
    cy.cleanTestCourses();
    cy.demoAdminLogin();
    cy.get('[data-cy="administrationMenuButton"]').click();
    cy.get('[data-cy="manageCoursesMenuButton"]').click({ force: true });
  });

  afterEach(() => {
    cy.logout();
  });

  it('login creates a course execution adds a teacher though the form and deletes the teacher and course execution', () => {
    cy.createCourseExecution('Demo Course', 'TEST-AO3', 'Spring Semester');
    cy.addUserThroughForm(
      'TEST-AO3',
      'User1',
      'user1',
      'test@mail.com',
      'TEACHER'
    );
    cy.closeUserCreationDialog();
    cy.checkTeacherCount('TEST-AO3', '1');
    cy.deleteUser('user1', 'TEST-AO3');
    cy.checkTeacherCount('TEST-AO3', '0');
    cy.deleteCourseExecution('TEST-AO3');
  });

  it('login creates two course executions and deletes it', () => {
    cy.createCourseExecution('Demo Course', 'TEST-AO3', 'Spring Semester');
    cy.log('try to create with the same name');
    cy.createCourseExecution('Demo Course', 'TEST-AO3', 'Spring Semester');
    cy.closeErrorMessage();
    cy.log('close dialog');
    cy.get('[data-cy="cancelButton"]').click();
    cy.deleteCourseExecution('TEST-AO3');
  });

  it('login creates FROM a course execution and deletes it', () => {
    cy.createFromCourseExecution('Demo Course', 'TEST-AO3', 'Winter Semester');
    cy.deleteCourseExecution('TEST-AO3');
  });

  it('login as admin, creates a new course execution, try to add the same student twice, delete student and the course execution', () => {
    cy.createCourseExecution('Demo Course', 'TEST-AO3', 'Spring Semester');
    cy.addUserThroughForm(
      'TEST-AO3',
      'User1',
      'user1',
      'test@mail.com',
      'STUDENT'
    );
    cy.closeUserCreationDialog();
    cy.addUserThroughForm(
      'TEST-AO3',
      'User1',
      'user1',
      'test@mail.com',
      'STUDENT'
    );
    cy.contains('Error: Duplicate user: user1');
    cy.closeErrorMessage();
    cy.closeUserCreationDialog();
    cy.deleteUser('user1', 'TEST-AO3');
    cy.checkStudentCount('TEST-AO3', '0');
    cy.deleteCourseExecution('TEST-AO3');
  });
});

describe('Exeternal Users Management', () => {

    beforeEach(() => {
        cy.demoAdminLogin();
        cy.get('[data-cy="administrationMenuButton"]').click();
        cy.get('[data-cy="manageCoursesMenuButton"]').click();
        cy.createCourseExecution('Demo Course', 'TEST-AO1', 'Spring Semester');
        cy.addStudentThroughForm('TEST-AO1', 'User1', 'test1@mail.com');
        cy.createCourseExecution('Demo Course', 'TEST-AO2', 'Summer Semester');
        cy.addStudentThroughForm('TEST-AO2', 'User2', 'test2@mail.com');
        cy.get('[data-cy="administrationMenuButton"]').click();
        cy.get('[data-cy="manageExeternalUsersButton"]').click();

    })

    afterEach(() => {
        cy.get('[data-cy="administrationMenuButton"]').click();
        cy.get('[data-cy="manageCoursesMenuButton"]').click();
        cy.deleteCourseExecution('TEST-AO1');
        cy.get('[data-cy="administrationMenuButton"]').click();
        cy.get('[data-cy="manageCoursesMenuButton"]').click();
        cy.deleteCourseExecution('TEST-AO2');

        cy.logout();
    });

    it('login creates two external course executions adds a student to each through the form, and deletes them both', () => {
        cy.wait(2000);
        cy.get('[data-cy="courseSelectionDropDown"]').click({force:true}).type('{downarrow}{enter}', {force: true});
        cy.contains('test1@mail.com').parent().children().eq(0).click();
        cy.get('[data-cy="deleteSelectedUsersButton"]').click();
        cy.get('[data-cy="courseSelectionDropDown"]').click({force:true}).type('{downarrow}{enter}', {force: true});
        cy.contains('test2@mail.com').parent().children().eq(0).click();
        cy.get('[data-cy="deleteSelectedUsersButton"]').click();
    });

});
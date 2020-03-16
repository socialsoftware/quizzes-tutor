describe('Administration walkthrough', () => {
  beforeEach(() => {
    cy.demoAdminLogin()
  })

  afterEach(() => {
    cy.contains('Logout').click()
  })

  it('login creates and deletes a course execution', () => {
    cy.createCourseExecution('Demo Course','TEST-AO3','Spring Semester')

    cy.deleteCourseExecution('TEST-AO3')
  });

  it('login creates two course executions and deletes it', () => {
    cy.createCourseExecution('Demo Course','TEST-AO3','Spring Semester')

    cy.log('try to create with the same name')
    cy.createCourseExecution('Demo Course','TEST-AO3','Spring Semester')

    cy.closeErrorMessage()

    cy.log('close dialog')
    cy.get('[data-cy="cancelButton"]').click()

    cy.deleteCourseExecution('TEST-AO3')
  });

  it('login creates from a course execution and deletes it', () => {
    cy.createFromCourseExecution('Demo Course','TEST-AO3','Spring Semester')

    cy.deleteCourseExecution('TEST-AO3')
  });

});

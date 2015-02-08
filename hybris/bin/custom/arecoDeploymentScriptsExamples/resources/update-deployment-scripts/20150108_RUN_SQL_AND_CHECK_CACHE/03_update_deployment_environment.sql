update {table_prefix}arenvironmentlp
set p_description = 'Please remove this environment. It was used for an integration test. UPDATED'
where itempk = (select e.pk
    from {table_prefix}arenvironment e
    where e.p_name = 'DUMMY_ENVIRONMENT'
    )
/* There isn't any need to filter the language because there is only one row in junit_arenvironmentlp with the name of the environment. */

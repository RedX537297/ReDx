<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head> 
	<title>Developers database</title> 
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
	<h1>Developers</h1>
	<table>
		<tr>
			<th>Name</th>
			<th>Skills</th>
			<th></th>
		</tr>
		<tr th:each="developer : ${developers}">
			<td th:text="${developer.firstName + ' ' + developer.lastName}"></td>
			<td>
				<span th:each="skill,iterStat : ${developer.skills}">
					<span th:text="${skill.label}"/><th:block th:if="${!iterStat.last}">,</th:block>
				</span>
			</td>
			<td>
				<a th:href="@{/developer/{id}(id=${developer.id})}">view</a>
			</td>
		</tr>
	</table>
	<hr/>
	<form th:action="@{/developers}" method="post" enctype="multipart/form-data">
		<div>
			First name: <input name="firstName" />
		</div>
		<div>
			Last name: <input name="lastName" />
		</div>
		<div>
			Email: <input name="email" />
		</div>
		<div>
			<input type="submit" value="Create developer" name="button"/>
		</div>
	</form>
</body>
public class Developer {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	private String firstName;
	private String lastName;
	private String email;
	@ManyToMany
	private List<Skill> skills;

	public Developer() {
		super();
	}

	public Developer(String firstName, String lastName, String email,
			List<Skill> skills) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.skills = skills;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Skill> getSkills() {
		return skills;
	}

	public void setSkills(List<Skill> skills) {
		this.skills = skills;
	}

	public boolean hasSkill(Skill skill) {
		for (Skill containedSkill: getSkills()) {
			if (containedSkill.getId() == skill.getId()) {
				return true;
			}
		}
		return false;
	}

}
public class Skill {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    private String label;
    private String description;

    public Skill() {
		super();
    }

    public Skill(String label, String description) {
		super();
		this.label = label;
		this.description = description;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
    
}
public interface DeveloperRepository extends CrudRepository<Developer, Long> {

}
public interface SkillRepository extends CrudRepository<Skill, Long> {
	public List<Skill> findByLabel(String label);
}
public class DevelopersController {

	@Autowired
	DeveloperRepository repository;

	@Autowired
	SkillRepository skillRepository;

	@RequestMapping("/developer/{id}")
	public String developer(@PathVariable Long id, Model model) {
		model.addAttribute("developer", repository.findOne(id));
		model.addAttribute("skills", skillRepository.findAll());
		return "developer";
	}

	@RequestMapping(value="/developers",method=RequestMethod.GET)
	public String developersList(Model model) {
		model.addAttribute("developers", repository.findAll());
		return "developers";
	}

	@RequestMapping(value="/developers",method=RequestMethod.POST)
	public String developersAdd(@RequestParam String email, 
						@RequestParam String firstName, @RequestParam String lastName, Model model) {
		Developer newDeveloper = new Developer();
		newDeveloper.setEmail(email);
		newDeveloper.setFirstName(firstName);
		newDeveloper.setLastName(lastName);
		repository.save(newDeveloper);

		model.addAttribute("developer", newDeveloper);
		model.addAttribute("skills", skillRepository.findAll());
		return "redirect:/developer/" + newDeveloper.getId();
	}

	@RequestMapping(value="/developer/{id}/skills", method=RequestMethod.POST)
	public String developersAddSkill(@PathVariable Long id, @RequestParam Long skillId, Model model) {
		Skill skill = skillRepository.findOne(skillId);
		Developer developer = repository.findOne(id);

		if (developer != null) {
			if (!developer.hasSkill(skill)) {
				developer.getSkills().add(skill);
			}
			repository.save(developer);
			model.addAttribute("developer", repository.findOne(id));
			model.addAttribute("skills", skillRepository.findAll());
			return "redirect:/developer/" + developer.getId();
		}

		model.addAttribute("developers", repository.findAll());
		return "redirect:/developers";
	}

}public class Application implements CommandLineRunner {

    @Autowired
    DeveloperRepository developerRepository;

    @Autowired
    SkillRepository skillRepository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
public void run(String... args) throws Exception {
	Skill javascript = new Skill("javascript", "Javascript language skill");
	Skill ruby = new Skill("ruby", "Ruby language skill");
	Skill emberjs = new Skill("emberjs", "Emberjs framework");
	Skill angularjs = new Skill("angularjs", "Angularjs framework");

	skillRepository.save(javascript);
	skillRepository.save(ruby);
	skillRepository.save(emberjs);
	skillRepository.save(angularjs);

	List<Developer> developers = new LinkedList<Developer>();
	developers.add(new Developer("John", "Smith", "john.smith@example.com", 
			Arrays.asList(new Skill[] { javascript, ruby })));
	developers.add(new Developer("Mark", "Johnson", "mjohnson@example.com", 
			Arrays.asList(new Skill[] { emberjs, ruby })));
	developers.add(new Developer("Michael", "Williams", "michael.williams@example.com", 
			Arrays.asList(new Skill[] { angularjs, ruby })));
	developers.add(new Developer("Fred", "Miller", "f.miller@example.com", 
			Arrays.asList(new Skill[] { emberjs, angularjs, javascript })));
	developers.add(new Developer("Bob", "Brown", "brown@example.com", 
			Arrays.asList(new Skill[] { emberjs })));
	developerRepository.save(developers);
}
</html>

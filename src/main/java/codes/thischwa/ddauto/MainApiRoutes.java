package codes.thischwa.ddauto;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "main", description = "The routes.")
public interface MainApiRoutes {

	@Operation(summary = "Checks, if the 'host' exists and is configured.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Host exists and is configured.", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, examples = {
					@ExampleObject(value = "Host found.") })),
			@ApiResponse(responseCode = "404", description = "Host doesn't exists.", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, examples = {
					@ExampleObject(value = "Host not found.") })) })
	@GetMapping(value = "/exist/{host}", produces = MediaType.TEXT_PLAIN_VALUE)
	ResponseEntity<String> exist(
			@Parameter(description = "The desired host to check.", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, examples = {
					@ExampleObject(value = "mydyndns.domain.com") }))
			@PathVariable String host);


	@Operation(summary = "Updates the desired IP addresses of the 'host', if the 'apitoken' belongs to the host. If both parameters for IP addresses aren't set, an attempt is made to fetch the remote IP.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Updates the desired IP addresses of the 'host', if the 'apitoken' belongs to the host. If both parameters for IP addresses aren't set, an attempt is made to fetch the remote IP.", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, examples = {
					@ExampleObject(value = "Update successful.") })),
			@ApiResponse(responseCode = "400", description = "If the 'apitoken' doesn't match the 'host', IP addresses aren't valid or the remote IP couldn't determine."),
			@ApiResponse(responseCode = "500", description = "If the zone update fails.", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, examples = {
					@ExampleObject(value = "Zone update failed.") })) })
	@GetMapping(value = "/update/{host}", produces = MediaType.TEXT_PLAIN_VALUE)
	ResponseEntity<String> update(
			@Parameter(description = "The host, for which the IPs must be updated.", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, examples = {
					@ExampleObject(value = "mydyndns.domain.com") }))
			@PathVariable String host, @RequestParam String apitoken, @RequestParam(name = "ipv4", required = false) String ipv4Str,
			@RequestParam(name = "ipv6", required = false) String ipv6Str, HttpServletRequest req);


	@Operation(summary = "Determine the IP settings of the 'host' and returns it as formatted plain text.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Formatted plain text with the IP settings of the 'host'"),
			@ApiResponse(responseCode = "404", description = "If the 'host' isn't configured.", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, examples = {
					@ExampleObject(value = "Host not found.") })),
			@ApiResponse(responseCode = "500", description = "If the zone info fails.", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, examples = {
					@ExampleObject(value = "Zone info failed.") })) })
	@GetMapping(value = "/info/{host}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> info(
			@Parameter(description = "The host, for which the IPs must be identified.", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, examples = {
					@ExampleObject(value = "mydyndns.domain.com") }))
			@PathVariable String host);

	@Operation(summary = "Generates basic memory informations")
	@ApiResponse(responseCode = "200", description = "Textual information about the memory usage.")
	@GetMapping(value = "meminfo", produces = MediaType.TEXT_PLAIN_VALUE)
	ResponseEntity<String> getMemoryStatistics();

}